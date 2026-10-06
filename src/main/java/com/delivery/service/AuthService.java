package com.delivery.service;

import com.delivery.dto.request.LoginRequest;
import com.delivery.dto.request.RegisterRequest;
import com.delivery.dto.response.AuthResponse;
import com.delivery.entity.Rol;
import com.delivery.entity.Usuario;
import com.delivery.exception.InvalidStatusException;
import com.delivery.repository.UsuarioRepository;
import com.delivery.security.CustomUserDetails;
import com.delivery.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse registrar(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new InvalidStatusException("El email ya se encuentra registrado: " + request.getEmail());
        }

        Usuario nuevoUsuario = Usuario.builder()
                .nombre(request.getNombre())
                .direccion(request.getDireccion())
                .telefono(request.getTelefono())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(Rol.CLIENTE)
                .build();

        usuarioRepository.save(nuevoUsuario);

        CustomUserDetails userDetails = new CustomUserDetails(nuevoUsuario);
        String token = jwtService.generateToken(userDetails, nuevoUsuario.getRol().name());

        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .email(nuevoUsuario.getEmail())
                .rol(nuevoUsuario.getRol().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidStatusException("Credenciales invalidas"));

        CustomUserDetails userDetails = new CustomUserDetails(usuario);
        String token = jwtService.generateToken(userDetails, usuario.getRol().name());

        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .build();
    }
}