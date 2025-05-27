package com.generation.blogpessoal.util;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.model.Tema;
import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;

public class TestBuilder {

    public static Usuario criarUsuario(Long id, String nome, String email, String senha) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome(nome);
        usuario.setUsuario(email);
        usuario.setSenha(senha);
        usuario.setFoto("-");
        return usuario;
    }
    
    public static UsuarioLogin criarUsuarioLogin(String email, String senha) {
        UsuarioLogin usuarioLogin = new UsuarioLogin();
        usuarioLogin.setId(null);
        usuarioLogin.setNome("");
        usuarioLogin.setUsuario(email);
        usuarioLogin.setSenha(senha);
        usuarioLogin.setFoto("");
        usuarioLogin.setToken("");
        return usuarioLogin;
    }

    public static Tema criarTema(Long id, String descricao) {
        Tema tema = new Tema();
        tema.setId(id);
        tema.setDescricao(descricao);
        return tema;
    }
    
    public static Postagem criarPostagem(Long id, String titulo, String texto, Tema tema, Usuario usuario) {
        Postagem postagem = new Postagem();
        postagem.setId(id);
        postagem.setTitulo(titulo);
        postagem.setTexto(texto);
        postagem.setData(null);
        postagem.setTema(tema);
        postagem.setUsuario(usuario);
        return postagem;
    }
    
    public static Usuario criarUsuarioRoot() {
        return criarUsuario(null, "Root", "root@root.com", "rootroot");
    }

    public static Usuario criarUsuarioTeste() {
        return criarUsuario(null, "Teste", "teste@email.com", "teste123");
    }
    
	public static Tema criarTemaTeste() {
		return criarTema(null, "Tema teste");
	}
}

