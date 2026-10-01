# PocketBudget

Aplicação mobile de gestão financeira pessoal desenvolvida para Android, criada para ajudar no controle prático do seu dinheiro, organizando entradas e saídas e calculando seu saldo em tempo real.

📌 Para que serve o projeto

O **PocketBudget** foi desenvolvido para resolver a dificuldade de acompanhar gastos diários e manter a saúde financeira em dia. Com ele, o usuário pode:
- Cadastrar uma conta pessoal de forma simples.
- Acompanhar o saldo total disponível atualizado automaticamente.
- Visualizar um resumo claro entre receitas (entradas) e despesas (saídas).
- Registrar novas movimentações financeiras em poucos cliques.
- Consultar e excluir o histórico de transações com sincronização imediata na nuvem.

🧠 Como foi feito

O aplicativo foi construído do zero utilizando a linguagem **Kotlin** e as práticas modernas de desenvolvimento Android recomendadas pela Google:
- **Interface Declarativa**: Desenvolvida com **Jetpack Compose** e **Material Design 3**, garantindo um visual moderno, fluido e adaptável.
- **Navegação**: Gerenciada pelo **Jetpack Navigation Compose**, permitindo transições suaves entre as telas de Login, Cadastro, Home e Adicionar Movimentação.
- **Autenticação de Usuários**: Integrado ao **Firebase Authentication** para garantir login seguro e personalizado para cada usuário.
- **Persistência na Nuvem**: Utiliza o **Cloud Firestore** para salvar as movimentações em tempo real associadas à conta de cada usuário.

📁 Estrutura do projeto

app/src/main/java/com/example/pocketbudget/ — código fonte principal da aplicação
├── ui/theme/ — arquivos de estilização e temas em Jetpack Compose (Color.kt, Theme.kt, Type.kt)
├── AddTransactionScreen.kt — tela para adicionar novas movimentações financeiras
├── AppDatabase.kt — configurações do banco de dados local Room
├── HomeScreen.kt — tela principal com resumo financeiro e lista de movimentações
├── LoginScreen.kt — tela de autenticação de usuários
├── MainActivity.kt — ponto de entrada da aplicação e controle de navegação
├── RegisterScreen.kt — tela de cadastro de novos usuários com apelido
├── Transaction.kt — modelo de dados para movimentações
├── TransactionDao.kt — interface de acesso a dados locais (DAO)
└── TransactionEntity.kt — entidade do banco de dados local

app/src/main/res/ — recursos visuais, ícones e arquivos de layout
app/google-services.json — arquivo de configuração do Firebase (deve ser configurado localmente)
build.gradle.kts — scripts de compilação e dependências do Gradle

🛠️ Tecnologias

Kotlin
Jetpack Compose (Interface de Usuário)
Firebase Authentication (Gestão de Usuários)
Cloud Firestore (Banco de Dados em Nuvem)
Material Design 3 (Componentes Visuais)
Jetpack Navigation (Fluxo entre telas)

🚀 Como executar

1. Clone este repositório para o seu computador:
   git clone https://github.com/NascimentoJS/PocketBudget.git

2. Abra o projeto no Android Studio (versão Hedgehog ou superior).

3. Configure o Firebase no seu projeto:
   - Acesse o Firebase Console e crie um novo projeto.
   - Adicione um aplicativo Android com o pacote `com.example.pocketbudget`.
   - Baixe o arquivo `google-services.json` e cole-o dentro da pasta `app/` do seu projeto no Android Studio.
   - Ative os serviços de **Authentication (E-mail/Senha)** e **Cloud Firestore** no Firebase Console.

4. Realize a sincronização das dependências (Gradle Sync).

5. Execute a aplicação em um emulador Android ou em um dispositivo físico via USB.

📄 Funcionalidades disponíveis

O projeto possui os seguintes recursos completos:
Autenticação de Usuários (Login e Cadastro com salvamento de apelido/perfil)
Cálculo e Exibição de Saldo em Tempo Real
Resumo de Receitas e Despesas
Histórico de Movimentações Financeiras com Scroll
Registro de Novas Entradas e Saídas
Exclusão de Movimentações com Sincronização no Firestore

📌 Observação

Não compartilhe o seu arquivo `google-services.json` em repositórios públicos por questões de segurança. Mantenha as dependências do Gradle atualizadas para garantir o funcionamento correto de todas as bibliotecas.

Desenvolvido para fins de apresentação e organização do projeto
