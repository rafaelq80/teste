package com.generation.blogpessoal.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.security.JwtService;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	public Optional<Usuario> cadastrarUsuario(Usuario usuario) {
		if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent()) {
			return Optional.empty();
		}

		usuario.setSenha(encoder.encode(usuario.getSenha()));
		return Optional.of(usuarioRepository.save(usuario));
	}

	public Optional<Usuario> atualizarUsuario(Usuario usuario) {
		if (!usuarioRepository.existsById(usuario.getId())) {
			return Optional.empty();
		}

		Optional<Usuario> usuarioExistente = usuarioRepository.findByUsuario(usuario.getUsuario());
		
		if (usuarioExistente.isPresent() && !usuarioExistente.get().getId().equals(usuario.getId())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe!", null);
		}

		usuario.setSenha(encoder.encode(usuario.getSenha()));
		return Optional.of(usuarioRepository.save(usuario));
	}

	public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin) {
		if (!usuarioLogin.isPresent()) {
			return Optional.empty();
		}

		UsuarioLogin login = usuarioLogin.get();
		
		var credenciais = new UsernamePasswordAuthenticationToken(login.getUsuario(), login.getSenha());
		Authentication authentication = authenticationManager.authenticate(credenciais);

		if (!authentication.isAuthenticated()) {
			return Optional.empty();
		}

		return usuarioRepository.findByUsuario(login.getUsuario())
			.map(usuario -> {
				login.setId(usuario.getId());
				login.setNome(usuario.getNome());
				login.setFoto(usuario.getFoto());
				login.setToken("Bearer " + jwtService.generateToken(login.getUsuario()));
				login.setSenha("");
				return login;
			});
	}
}