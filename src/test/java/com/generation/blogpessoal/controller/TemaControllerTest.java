package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

import com.generation.blogpessoal.model.Tema;
import com.generation.blogpessoal.repository.TemaRepository;
import com.generation.blogpessoal.service.UsuarioService;
import com.generation.blogpessoal.util.TestBuilder;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class TemaControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	private TemaRepository temaRepository;
	
	@Autowired
	private UsuarioService usuarioService;
	
	private static final String USUARIO_ROOT_EMAIL = "root@root.com";
	private static final String USUARIO_ROOT_SENHA = "rootroot";
	private static final String BASE_URL_TEMAS = "/temas";

	@BeforeAll
	void start(){
		temaRepository.deleteAll();
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuarioRoot());
	}
	
	@Test
	@DisplayName("✔ 01 - Deve cadastrar um novo tema com sucesso")
	public void deveCadastrarTema() {
		
		// Given
		Tema tema = TestBuilder.criarTema(null, "Tema 01");

		// When
		HttpEntity<Tema> requisicao = new HttpEntity<>(tema);
		ResponseEntity<Tema> resposta = testRestTemplate
				.withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
				.exchange(BASE_URL_TEMAS, HttpMethod.POST, requisicao, Tema.class);

		// Then
		assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
		assertEquals("Tema 01", resposta.getBody().getDescricao());
	}

	@Test
	@DisplayName("✔ 02 - Deve atualizar um tema existente")
	public void deveAtualizarUmTema() {
		
		//Given
		Tema tema = TestBuilder.criarTema(null, "Tema 02");
		Tema temaCadastrado = temaRepository.save(tema);
		
		Tema temaUpdate = TestBuilder.criarTema(temaCadastrado.getId(), "Tema 03");

		//When
		HttpEntity<Tema> requisicao = new HttpEntity<>(temaUpdate);

		ResponseEntity<Tema> resposta = testRestTemplate
				.withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
				.exchange(BASE_URL_TEMAS, HttpMethod.PUT, requisicao, Tema.class);

		//Then
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertEquals("Tema 03", resposta.getBody().getDescricao());
	}

	@Test
	@DisplayName("✔ 03 - Deve listar todos os temas")
	public void deveListarTodosTemas() {
		
		//Given
		temaRepository.save(TestBuilder.criarTema(null, "Tema 04"));
		temaRepository.save(TestBuilder.criarTema(null, "Tema 05"));

		//When
		ResponseEntity<Tema[]> resposta = testRestTemplate
				.withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
				.exchange(BASE_URL_TEMAS, HttpMethod.GET, null, Tema[].class);

		//Then
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
	}
	
	@Test
	@DisplayName("✔ 04 - Deve listar um tema específico - pelo id")
	public void deveListarUmTemaPorId() {
		
		//Given
		Tema tema = temaRepository.save(TestBuilder.criarTema(null, "Tema 06"));
		var id = tema.getId();
		
		//When
		ResponseEntity<Tema> resposta = testRestTemplate
				.withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
				.exchange(BASE_URL_TEMAS + "/" + id, HttpMethod.GET, null, Tema.class);

		//Then
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
	}
	
	@Test
	@DisplayName("✔ 05 - Deve listar todos os temas - por descrição")
	public void deveListarTemasPorDescricao() {
		
		//Given
		temaRepository.save(TestBuilder.criarTema(null, "Tema 07"));
		String descricao = "08";
				
		//When
		ResponseEntity<Tema[]> resposta = testRestTemplate
				.withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
				.exchange(BASE_URL_TEMAS + "/descricao/" + descricao, HttpMethod.GET, null, Tema[].class);

		//Then
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
	}
	
	@Test
	@DisplayName("✔ 06 - Deve deletar um tema")
	public void deveDeletarUmTema() {
		
		//Given
		Tema tema = temaRepository.save(TestBuilder.criarTema(null, "Tema 08"));
		var id = tema.getId();
				
		//When
		ResponseEntity<Void> resposta = testRestTemplate
				.withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
				.exchange(BASE_URL_TEMAS + "/" + id, HttpMethod.DELETE, null, Void.class);

		//Then
		assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());

	}
	
}
