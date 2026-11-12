package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testAccountingBalanceCalculations() {
    val incoming1 = 5000.0
    val incoming2 = 2500.0
    val outgoing1 = -1000.0
    val outgoing2 = -800.0

    val txs = listOf(incoming1, incoming2, outgoing1, outgoing2)

    val totalCredit = txs.filter { it > 0 }.sum()
    val totalDebit = txs.filter { it < 0 }.sumOf { abs(it) }
    val netBalance = totalCredit - totalDebit

    assertEquals(7500.0, totalCredit, 0.001)
    assertEquals(1800.0, totalDebit, 0.001)
    assertEquals(5700.0, netBalance, 0.001)
    assertTrue(netBalance > 0)
  }

  @Test
  fun testTransferReciprocity() {
    val transferAmount = 1500.0
    val sourceDelta = -transferAmount
    val destDelta = transferAmount

    assertEquals(0.0, sourceDelta + destDelta, 0.001)
  }
}
