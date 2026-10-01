package com.example.pocketbudget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddTransactionScreen(
        onBack: () -> Unit,
        onAddTransaction: (Transaction) -> Unit
) {
        var description by remember { mutableStateOf("") }
        var amount by remember { mutableStateOf("") }
        var isIncome by remember { mutableStateOf(true) }
        var errorMessage by remember { mutableStateOf("") }

        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {

                // ----------------------------------------------------
                // CABEÇALHO
                // ----------------------------------------------------

                Text(
                        text = "Nova movimentação",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                        text = "Registre uma entrada ou saída do seu dinheiro.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(28.dp))

                // ----------------------------------------------------
                // TIPO DE MOVIMENTAÇÃO
                // ----------------------------------------------------

                Text(
                        text = "Tipo de movimentação",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                        if (isIncome) {
                                Button(
                                        onClick = {
                                                isIncome = true
                                        },
                                        modifier = Modifier
                                                .weight(1f)
                                                .height(52.dp),
                                        shape = RoundedCornerShape(16.dp)
                                ) {
                                        Text(
                                                text = "↑ Receita",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold
                                        )
                                }
                        } else {
                                OutlinedButton(
                                        onClick = {
                                                isIncome = true
                                        },
                                        modifier = Modifier
                                                .weight(1f)
                                                .height(52.dp),
                                        shape = RoundedCornerShape(16.dp)
                                ) {
                                        Text(
                                                text = "↑ Receita",
                                                fontSize = 15.sp
                                        )
                                }
                        }

                        if (!isIncome) {
                                Button(
                                        onClick = {
                                                isIncome = false
                                        },
                                        modifier = Modifier
                                                .weight(1f)
                                                .height(52.dp),
                                        shape = RoundedCornerShape(16.dp)
                                ) {
                                        Text(
                                                text = "↓ Despesa",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold
                                        )
                                }
                        } else {
                                OutlinedButton(
                                        onClick = {
                                                isIncome = false
                                        },
                                        modifier = Modifier
                                                .weight(1f)
                                                .height(52.dp),
                                        shape = RoundedCornerShape(16.dp)
                                ) {
                                        Text(
                                                text = "↓ Despesa",
                                                fontSize = 15.sp
                                        )
                                }
                        }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ----------------------------------------------------
                // DESCRIÇÃO
                // ----------------------------------------------------

                Text(
                        text = "Descrição",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                        value = description,
                        onValueChange = {
                                description = it
                                errorMessage = ""
                        },
                        placeholder = {
                                Text("Ex: Salário, Mercado, Aluguel...")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ----------------------------------------------------
                // VALOR
                // ----------------------------------------------------

                Text(
                        text = "Valor",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                        value = amount,
                        onValueChange = {
                                amount = it
                                errorMessage = ""
                        },
                        placeholder = {
                                Text("0,00")
                        },
                        prefix = {
                                Text(
                                        text = "R$ ",
                                        fontWeight = FontWeight.SemiBold
                                )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                        )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ----------------------------------------------------
                // ERRO
                // ----------------------------------------------------

                if (errorMessage.isNotEmpty()) {
                        Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 13.sp,
                                modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                }

                // ----------------------------------------------------
                // SALVAR
                // ----------------------------------------------------

                Button(
                        onClick = {
                                val cleanDescription = description.trim()
                                val cleanAmount = amount
                                        .replace(",", ".")
                                        .trim()

                                val parsedAmount = cleanAmount.toDoubleOrNull()

                                when {
                                        cleanDescription.isEmpty() -> {
                                                errorMessage = "Digite uma descrição."
                                        }

                                        parsedAmount == null -> {
                                                errorMessage = "Digite um valor válido."
                                        }

                                        parsedAmount <= 0 -> {
                                                errorMessage = "O valor deve ser maior que zero."
                                        }

                                        else -> {
                                                val transaction = Transaction(
                                                        description = cleanDescription,
                                                        amount = parsedAmount,
                                                        isIncome = isIncome
                                                )

                                                onAddTransaction(transaction)
                                        }
                                }
                        },
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                        )
                ) {
                        Text(
                                text = "Salvar movimentação",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                        )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ----------------------------------------------------
                // CANCELAR
                // ----------------------------------------------------

                OutlinedButton(
                        onClick = {
                                onBack()
                        },
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                        shape = RoundedCornerShape(16.dp)
                ) {
                        Text(
                                text = "Cancelar",
                                fontSize = 15.sp
                        )
                }
        }
}