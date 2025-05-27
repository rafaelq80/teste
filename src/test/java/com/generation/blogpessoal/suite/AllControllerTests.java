package com.generation.blogpessoal.suite;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.generation.blogpessoal.controller.PostagemControllerTest;
import com.generation.blogpessoal.controller.TemaControllerTest;
import com.generation.blogpessoal.controller.UsuarioControllerTest;

@Suite
@SelectClasses({
    TemaControllerTest.class,
    UsuarioControllerTest.class,
    PostagemControllerTest.class
})
public class AllControllerTests {
}
