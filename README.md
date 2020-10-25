# Programação Distribuida (Meta 1)

## Introdução

Neste trabalho pretende-se que seja desenvolvido um sistema distribuído de comunicação em
rede, com características semelhantes ao *Slack*. A implementação será feita com a linguagem
Java, utilizando conceitos estudados na unidade curricular. O sistema será essencialmente
composto por dois tipos de aplicações: **cliente**, com a qual os utilizadores irão interagir,
e **servidor**, que será responsável por toda a lógica de negócio e pela persistência da informação.
Nas próximas seções são descritos os requisitos funcionais do sistema e os requisitos arquiteturais
para cada uma das aplicações.


## 1. Requisitos Funcionais

O sistema de comunicação em rede pretendido deve oferecer as seguintes funcionalidades:

- **Registo de novos utilizadores**, que são caracterizados por um nome, um *username*, uma
*password* e uma fotografia;
- **Autenticação de utilizadores** já registados através de um *username* e de uma *password*;
- **Criação, edição e eliminação de canais**, que são grupos onde diversos utilizadores podem
conversar entre si. Cada canal é caraterizado pelas seguintes informações: nome, descrição,
*password* de acesso e identificação do utilizador que o criou. Para aceder a um canal e
conversar com os restantes membros, um utilizador precisa de inserir a *password* de acesso.
Após ter sido criado, um canal só pode ser editado ou eliminado pelo utilizador que o criou;
