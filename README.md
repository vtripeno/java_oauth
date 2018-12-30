#Projeto de demonstração para testes com autenticação OAUT2 junto com Springboot 1.5

##Chamadas

- **POST**
    
    A chamada do método POST deve ser executada com o tipo de *Authorization* Basic Auth, onde **Username=foo** e **Password=bar**.
    
    localhost:8080/security/oauth/token?grant_type=password&username=userteste&password=teste
    
    Após a chamada, o retorno será:
    
    ``
    {
        "access_token": "d8f678e2-26e5-4dae-b275-43bf33480e85",
        "token_type": "bearer",
        "refresh_token": "1226ed88-e4a6-4c64-958d-730a5cca4a47",
        "expires_in": 43199,
        "scope": "user_info read write"
    }
    ``
    
    Para fazer a chamada de qualquer outra rota, é necessário utilizar o código **access_token** com o tipo de *Authorization* Bearer Token, onde **Token=d8f678e2-26e5-4dae-b275-43bf33480e85**, para garantir que está sendo autenticado, como por exemplo:
    
   http://localhost:8080/security/waffle
   