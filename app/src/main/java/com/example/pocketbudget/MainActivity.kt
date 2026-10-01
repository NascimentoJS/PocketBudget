package com.example.pocketbudget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pocketbudget.ui.theme.PocketBudgetTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        setContent {
            PocketBudgetTheme {

                val navController = rememberNavController()
                val scope = rememberCoroutineScope()

                var currentUser by remember { mutableStateOf<FirebaseUser?>(null) }
                var authReady by remember { mutableStateOf(false) }
                var userNickname by remember { mutableStateOf("") }
                var transactions by remember { mutableStateOf(emptyList<Transaction>()) }
                var errorMessage by remember { mutableStateOf("") }

                // Observa o estado de autenticação
                DisposableEffect(Unit) {
                    val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                        currentUser = firebaseAuth.currentUser
                        authReady = true
                    }
                    auth.addAuthStateListener(authStateListener)
                    onDispose { auth.removeAuthStateListener(authStateListener) }
                }

                // Carrega dados do utilizador e movimentações no Firestore
                LaunchedEffect(currentUser?.uid) {
                    transactions = emptyList()
                    val user = currentUser

                    if (user != null) {
                        // Busca o apelido do perfil do utilizador
                        firestore.collection("users").document(user.uid).get()
                            .addOnSuccessListener { document ->
                                userNickname = document.getString("nickname") ?: ""
                            }

                        // Busca o histórico de movimentações
                        firestore.collection("users").document(user.uid)
                            .collection("transactions").get()
                            .addOnSuccessListener { result ->
                                transactions = result.documents.mapNotNull { document ->
                                    val description = document.getString("description")
                                    val amount = document.getDouble("amount")
                                    val isIncome = document.getBoolean("isIncome")

                                    if (description != null && amount != null && isIncome != null) {
                                        Transaction(
                                            id = 0,
                                            description = description,
                                            amount = amount,
                                            isIncome = isIncome,
                                            firestoreId = document.id
                                        )
                                    } else null
                                }
                            }
                            .addOnFailureListener { exception ->
                                errorMessage = "Erro ao carregar dados: ${exception.message}"
                            }
                    }
                }

                if (!authReady) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    NavHost(
                        navController = navController,
                        startDestination = if (currentUser != null) "home" else "login"
                    ) {

                        // LOGIN
                        composable("login") {
                            LoginScreen(
                                onLogin = { email, password ->
                                    errorMessage = ""
                                    auth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                navController.navigate("home") {
                                                    popUpTo("login") { inclusive = true }
                                                }
                                            } else {
                                                errorMessage = task.exception?.message ?: "Não foi possível entrar."
                                            }
                                        }
                                },
                                onCreateAccount = {
                                    navController.navigate("register")
                                }
                            )
                        }

                        // REGISTO
                        composable("register") {
                            RegisterScreen(
                                onRegister = { nickname, email, password ->
                                    errorMessage = ""
                                    auth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                val uid = auth.currentUser?.uid
                                                if (uid != null) {
                                                    // Salva o apelido no documento do utilizador no Firestore
                                                    val userData = hashMapOf("nickname" to nickname)
                                                    firestore.collection("users").document(uid).set(userData)
                                                }

                                                navController.navigate("home") {
                                                    popUpTo("register") { inclusive = true }
                                                }
                                            } else {
                                                errorMessage = task.exception?.message ?: "Não foi possível criar a conta."
                                            }
                                        }
                                },
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // HOME
                        composable("home") {
                            PocketBudgetScreen(
                                userName = userNickname,
                                transactions = transactions,
                                errorMessage = errorMessage,
                                onAddMovement = {
                                    errorMessage = ""
                                    navController.navigate("add")
                                },
                                onDeleteTransaction = { transaction ->
                                    val user = auth.currentUser
                                    if (user != null) {
                                        scope.launch {
                                            firestore.collection("users").document(user.uid)
                                                .collection("transactions").document(transaction.firestoreId)
                                                .delete()
                                                .addOnSuccessListener {
                                                    transactions = transactions.filter { it.firestoreId != transaction.firestoreId }
                                                    errorMessage = ""
                                                }
                                                .addOnFailureListener { exception ->
                                                    errorMessage = "Erro ao excluir: ${exception.message}"
                                                }
                                        }
                                    }
                                },
                                onLogout = {
                                    auth.signOut()
                                    transactions = emptyList()
                                    userNickname = ""
                                    errorMessage = ""
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // ADICIONAR MOVIMENTAÇÃO
                        composable("add") {
                            AddTransactionScreen(
                                onBack = { navController.popBackStack() },
                                onAddTransaction = { transaction ->
                                    val user = auth.currentUser
                                    if (user != null) {
                                        val data = hashMapOf(
                                            "description" to transaction.description,
                                            "amount" to transaction.amount,
                                            "isIncome" to transaction.isIncome
                                        )

                                        firestore.collection("users").document(user.uid)
                                            .collection("transactions").add(data)
                                            .addOnSuccessListener { documentReference ->
                                                val savedTransaction = transaction.copy(
                                                    id = 0,
                                                    firestoreId = documentReference.id
                                                )
                                                transactions = transactions + savedTransaction
                                                errorMessage = ""
                                                navController.popBackStack()
                                            }
                                            .addOnFailureListener { exception ->
                                                errorMessage = "Erro ao salvar: ${exception.message}"
                                            }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PocketBudgetScreen(
    userName: String,
    transactions: List<Transaction>,
    errorMessage: String,
    onAddMovement: () -> Unit,
    onDeleteTransaction: (Transaction) -> Unit,
    onLogout: () -> Unit
) {
    val totalIncome = transactions.filter { it.isIncome }.sumOf { it.amount }
    val totalExpenses = transactions.filter { !it.isIncome }.sumOf { it.amount }
    val balance = totalIncome - totalExpenses

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {

            // CABEÇALHO (Olá, [Apelido] + Botão Sair)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (userName.isNotBlank()) "Olá, $userName 👋" else "Olá 👋",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "PocketBudget",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = onLogout) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Sair",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // LISTA ROLÁVEL COM SCROLL
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                // MENSAGEM DE ERRO (SE HOUVER)
                if (errorMessage.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = errorMessage,
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // SALDO
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                text = "Saldo disponível",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "R$ %.2f".format(balance),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // RESUMO FINANCEIRO
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FinanceSummaryCard(
                            modifier = Modifier.weight(1f),
                            title = "Receitas",
                            amount = totalIncome,
                            description = "Entradas",
                            highlighted = true
                        )
                        FinanceSummaryCard(
                            modifier = Modifier.weight(1f),
                            title = "Despesas",
                            amount = totalExpenses,
                            description = "Saídas",
                            highlighted = false
                        )
                    }
                }

                // HISTÓRICO - TÍTULO
                item {
                    Text(
                        text = "Movimentações",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // ITENS DAS MOVIMENTAÇÕES OU MENSAGEM VAZIA
                if (transactions.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Nenhuma movimentação",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Adicione sua primeira movimentação.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(transactions) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onDelete = { onDeleteTransaction(transaction) }
                        )
                    }
                }

                // Espaço extra no final do scroll para não sobrepor o botão fixo
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        // BOTÃO FIXO ADICIONAR MOVIMENTAÇÃO NO RODAPÉ
        Button(
            onClick = onAddMovement,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Text(
                text = "Adicionar movimentação",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun FinanceSummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    amount: Double,
    description: String,
    highlighted: Boolean
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (highlighted) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(text = title, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "R$ %.2f".format(amount),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (transaction.isIncome) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = if (transaction.isIncome) "Receita" else "Despesa",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        color = if (transaction.isIncome) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (transaction.isIncome) "+ " else "- "}R$ %.2f".format(transaction.amount),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.isIncome) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                ) {
                    Text(text = "Excluir", fontSize = 11.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PocketBudgetPreview() {
    PocketBudgetTheme {
        PocketBudgetScreen(
            userName = "Gabriel",
            transactions = listOf(
                Transaction(description = "Salário", amount = 3500.00, isIncome = true),
                Transaction(description = "Supermercado", amount = 280.50, isIncome = false)
            ),
            errorMessage = "",
            onAddMovement = {},
            onDeleteTransaction = {},
            onLogout = {}
        )
    }
}