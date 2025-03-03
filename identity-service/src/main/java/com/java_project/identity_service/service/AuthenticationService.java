package com.java_project.identity_service.service;

import com.java_project.identity_service.dto.request.AuthenticationRequest;
import com.java_project.identity_service.dto.request.IntrospectRequest;
import com.java_project.identity_service.dto.request.LogoutRequest;
import com.java_project.identity_service.dto.request.RefreshRequest;
import com.java_project.identity_service.dto.response.AuthenticationResponse;
import com.java_project.identity_service.dto.response.IntrospectResponse;
import com.java_project.identity_service.entity.InvalidatedToken;
import com.java_project.identity_service.entity.User;
import com.java_project.identity_service.exception.AppException;
import com.java_project.identity_service.exception.ErrorCode;
import com.java_project.identity_service.repository.InvalidatedTokenRepository;
import com.java_project.identity_service.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) //Mặc định có kiểu private final
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal //Để không inject vào constructor
    @Value("${jwt.signerKey}") //Đọc giá trị từ file resources/application.yaml
    protected String SIGNER_KEY;

    @NonFinal //Để không inject vào constructor
    @Value("${jwt.valid-duration}") //Đọc giá trị từ file resources/application.yaml
    protected long VALID_DURATION;

    @NonFinal //Để không inject vào constructor
    @Value("${jwt.refreshable-duration}") //Đọc giá trị từ file resources/application.yaml
    protected long REFRESHABLE_DURATION;

    public AuthenticationResponse authenticate(AuthenticationRequest request){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
                .build();
    }

    private String generateToken(User user){
        //Tạo header token
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        //Payload
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issueTime(new Date())
                .issuer("")
                .claim("scope", buildScope(user)) //Tạo claim scope để map với spring security trong việc phân quyền
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString()) //Jwt id khi người dùng logout mà tk chưa hết hạn thì lưu lại jwt id
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    //Kiểm tra token có hợp lệ không
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();

        try {
            verifyToken(token, false);
        } catch (AppException exception) {
            return IntrospectResponse.builder()
                    .valid(false)
                    .build();
        }

        return IntrospectResponse.builder()
                .valid(true)
                .build();
    }

    private String buildScope(User user) {
        //Nối các role trong Set roles thành 1 chuỗi ngăn cách nhau bởi khoảng trắng
        StringJoiner stringJoiner = new StringJoiner(" ");

        //Sử dụng CollectionUtils để kiểm tra Set roles có rỗng không
        if(!CollectionUtils.isEmpty(user.getRoles())){
            //Tạo chuỗi
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if(!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> {
                        stringJoiner.add(permission.getName());
                    });
            });
        }

        return stringJoiner.toString();
    }

    public void logout(LogoutRequest request) throws JOSEException, ParseException {
        try {
            var token = request.getToken();
            var signToken = verifyToken(token, true);

            //Lấy token id từ JWT
            String Jti = signToken.getJWTClaimsSet().getJWTID();

            //Lấy thời gian hết hạn của token
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(Jti)
                    .expiryTime(expiryTime)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException exception) {
            log.info("Token already expired");
        }
    }

    //Trả về signedJWT cho các hàm khác dùng để lấy thông tin từ payload trong JWT
    private SignedJWT verifyToken(String token, boolean refresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        //Lấy expTime của token
        Date expiryTime = refresh ?
                new Date (signedJWT
                        .getJWTClaimsSet()
                        .getIssueTime()
                        .toInstant()
                        .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                        .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        //Nếu verify lỗi hoặc token hết hạn
        if (!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        //Nếu JWT id tồn tại trong bảng InvalidatedToken
        if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    //Cấp lại token (Cách làm ở dưới chỉ cung cấp lại token mới khi token cũ gần hết hạn còn hết hạn thì không được
    // do hàm verifyToken sẽ bị lỗi khi được gọi)
    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var token = request.getToken();
        var signToken = verifyToken(token, true);

        //Lấy token id từ JWT
        String Jti = signToken.getJWTClaimsSet().getJWTID();

        //Lấy thời gian hết hạn của token
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        //Lưu token chuẩn bị hết hạn vào DB
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(Jti)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        //Lấy username trong Jwt
        var username = signToken.getJWTClaimsSet().getSubject();
        //Lấy thông tin user từ DB
        var user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        //Tạo token mới từ thông tin user lấy trong DB
        var newToken = generateToken(user);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(newToken)
                .build();
    }
}
