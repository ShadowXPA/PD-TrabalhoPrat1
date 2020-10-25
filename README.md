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
- **Troca direta de mensagens entre utilizadores**, onde um utilizador apenas necessita de
saber o *username* do destinatário para conseguir enviar-lhe uma mensagem;
- **Partilha de ficheiros**, tanto de forma direta entre utilizadores como através de canais.
Neste último caso, após ter sido enviado para o sistema, o ficheiro fica disponível para
*download* por todos os membros do canal de destino. Podem ser partilhados ficheiros de
qualquer tipo. Após ser enviado, um ficheiro fica para sempre disponível para *download*
pelo(s) destinatário(s) (i.e., um utilizador específico ou um canal);
- **Listar todos os canais e utilizadores existentes** no sistema, sendo possível realizar
pesquisas e filtragens através dos atributos definidos para ambos. Todos os atributos devem
ser apresentados nas listagens obtidas;
- **Listar as *n* últimas mensagens trocadas** diretamente com um utilizador ou no contexto
de um canal;
- **Apresentar** os seguintes **dados estatísticos para cada canal**: número de utilizadores,
número de mensagens enviadas e número de ficheiros partilhados.

Ao implementar as funcionalidades descritas, tenha em consideração os seguintes aspetos:

- As funcionalidades só estão disponíveis para utilizadores autenticados, o que significa que
sem autenticação não se tem acesso ao sistema;
- Não podem ser criados utilizadores e canais com nomes iguais. Por outro lado, os nomes dos
ficheiros partilhados podem ser repetidos, sendo por isso necessário implementar estratégias
que permitam este aspeto;
- As operações de *upload* e *download* de ficheiros podem ser morosas e, em consequência,
bloquear a aplicação cliente durante um tempo exagerado na perspetiva do utilizador.
Desta forma, ambas as operações devem ser feitas em *background*, sendo o utilizador notificado
aquando do seu término;
- Existem aspetos relacionados com as funcionalidades descritas que estão omissos neste enunciado.
Isto significa que os alunos têm total liberdade para lidar com esses aspetos e implementarem
soluções de forma que melhor entenderem. Em caso de dúvidas, devem contactar um dos docentes para
as esclarecerem.

## 2. Requisitos Arquiteturais

A aquiterura geral do sistema aqui descrito é apresentada na Figura 1, sendo constituída pelos
seguintes elementos:

- Múltiplos (*n*) servidores, cada um com acesso a uma base de dados independente;
- Múltiplos (*n*) clientes, sendo que cada um comunica com um único servidor via UDP e TCP.


