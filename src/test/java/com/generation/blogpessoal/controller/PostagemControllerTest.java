package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.model.Tema;
import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.PostagemRepository;
import com.generation.blogpessoal.repository.TemaRepository;
import com.generation.blogpessoal.service.UsuarioService;
import com.generation.blogpessoal.util.TestBuilder;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class PostagemControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	private PostagemRepository postagemRepository;
	
	@Autowired
	private TemaRepository temaRepository;
	
	@Autowired
	private UsuarioService usuarioService;
	
	private Usuario usuarioTeste;
	private Tema temaTeste;
	
	private static final String USUARIO_ROOT_EMAIL = "root@root.com";
	private static final String USUARIO_ROOT_SENHA = "rootroot";
	private static final String BASE_URL_POSTAGENS = "/postagens";

	@BeforeAll
	void start(){
		
		postagemRepository.deleteAll();
		
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuarioRoot());
		
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(TestBuilder.criarUsuarioTeste());
		usuarioTeste = usuarioCadastrado.get();
		
		temaTeste = temaRepository.save(TestBuilder.criarTemaTeste());
	}
	
	@Test
	@DisplayName("✔ 01 - Deve cadastrar uma nova postagem com sucesso")
	public void deveCadastrarPostagem() {
		
		// Given
		Postagem postagem = TestBuilder.criarPostagem(null, "Postagem 01", "Texto da Postagem 01", temaTeste, usuarioTeste);

		// When
		HttpEntity<Postagem> requisicao = new HttpEntity<>(postagem);
		ResponseEntity<Postagem> resposta = testRestTemplate
				.withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
				.exchange(BASE_URL_POSTAGENS, HttpMethod.POST, requisicao, Postagem.class);

		// Then
		assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
		assertEquals("Postagem 01", resposta.getBody().getTitulo());
	}

	@Test
	@DisplayName("✔ 02 - Deve atualizar uma postagem existente")
	public void deveAtualizarUmPostagem() {
		
		//Given
		Postagem postagem = TestBuilder.criarPostagem(null, "Postagem 02", "Texto da Postagem 02", temaTeste, usuarioTeste);
		Postagem postagemCadastrado = postagemRepository.save(postagem);
		
		Postagem postagemUpdate = TestBuilder.criarPostagem(postagemCadastrado.getId(), "Postagem 03", "Texto da Postagem 03", temaTeste, usuarioTeste);

		//When
		HttpEntity<Postagem> requisicao = new HttpEntity<>(postagemUpdate);

		ResponseEntity<Postagem> resposta = testRestTemplate
				.withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
				.exchange(BASE_URL_POSTAGENS, HttpMethod.PUT, requisicao, Postagem.class);

		//Then
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertEquals("Postagem 03", resposta.getBody().getTitulo());
	}

	@Test
	@DisplayName("✔ 03 - Deve listar todas as postagens")
	public void deveListarTodosPostagems() {
		
		//Given
		postagemRepository.save(TestBuilder.criarPostagem(null, "Postagem 04", "Texto da Postagem 04", temaTeste, usuarioTeste));
		postagemRepository.save(TestBuilder.criarPostagem(null, "Postagem 05", "Texto da Postagem 05", temaTeste, usuarioTeste));

		//When
		ResponseEntity<Postagem[]> resposta = testRestTemplate
				.withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
				.exchange(BASE_URL_POSTAGENS, HttpMethod.GET, null, Postagem[].class);

		//Then
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
	}
	
	@Test
	@DisplayName("✔ 04 - Deve listar uma postagem específica - pelo id")
	public void deveListarUmPostagemPorId() {
		
		//Given
		Postagem postagem = postagemRepository.save(TestBuilder.criarPostagem(null, "Postagem 06", "Texto da Postagem 06", temaTeste, usuarioTeste));
		var id = postagem.getId();
		
		//When
		ResponseEntity<Postagem> resposta = testRestTemplate
				.withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
				.exchange(BASE_URL_POSTAGENS + "/" + id, HttpMethod.GET, null, Postagem.class);

		//Then
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
	}
	
	@Test
	@DisplayName("✔ 05 - Deve listar todas as postagens - por título")
	public void deveListarPostagemsPorDescricao() {
		
		//Given
		postagemRepository.save(TestBuilder.criarPostagem(null, "Postagem 07", "Texto da Postagem 07", temaTeste, usuarioTeste));
		String titulo = "07";
				
		//When
		ResponseEntity<Postagem[]> resposta = testRestTemplate
				.withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
				.exchange(BASE_URL_POSTAGENS + "/titulo/" + titulo, HttpMethod.GET, null, Postagem[].class);

		//Then
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
	}
	
	@Test
	@DisplayName("✔ 06 - Deve deletar uma postagem")
	public void deveDeletarUmPostagem() {
		
		//Given
		Postagem postagem = postagemRepository.save(TestBuilder.criarPostagem(null, "Postagem 08", "Texto da Postagem 08", temaTeste, usuarioTeste));
		var id = postagem.getId();
				
		//When
		ResponseEntity<Void> resposta = testRestTemplate
				.withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
				.exchange(BASE_URL_POSTAGENS + "/" + id, HttpMethod.DELETE, null, Void.class);

		//Then
		assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());

	}
	
}
