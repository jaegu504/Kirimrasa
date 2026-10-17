package com.wabulk.sender

import com.wabulk.sender.domain.model.AccountNode
import com.wabulk.sender.ui.screens.composer.ComposerViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ComposerViewModelTest {

    private lateinit var viewModel: ComposerViewModel

    @Before
    fun setUp() {
        viewModel = ComposerViewModel()
    }

    @Test
    fun `initial state has one bubble and personal account`() {
        val state = viewModel.uiState.value
        assertEquals(1, state.bubbles.size)
        assertEquals(AccountNode.PERSONAL, state.selectedAccount)
        assertEquals("friendly", state.selectedTone)
    }

    @Test
    fun `adding and updating bubbles reflect in state`() {
        viewModel.updateBubbleText(viewModel.uiState.value.bubbles[0].id, "Pesan pertama")
        assertEquals("Pesan pertama", viewModel.uiState.value.bubbles[0].text)

        viewModel.addBubble("Pesan kedua")
        assertEquals(2, viewModel.uiState.value.bubbles.size)
        assertEquals("Pesan kedua", viewModel.uiState.value.bubbles[1].text)
    }

    @Test
    fun `inserting variable appends variable to bubble text`() {
        val bubbleId = viewModel.uiState.value.bubbles[0].id
        viewModel.updateBubbleText(bubbleId, "Halo ")
        viewModel.insertVariable("{Nickname}", bubbleId)

        assertEquals("Halo {Nickname}", viewModel.uiState.value.bubbles[0].text)
    }

    @Test
    fun `switching account updates selectedAccount in state`() {
        viewModel.selectAccount(AccountNode.BUSINESS)
        assertEquals(AccountNode.BUSINESS, viewModel.uiState.value.selectedAccount)

        viewModel.selectAccount(AccountNode.DUAL_SYNC)
        assertEquals(AccountNode.DUAL_SYNC, viewModel.uiState.value.selectedAccount)
    }

    @Test
    fun `selecting tone updates tone in state`() {
        viewModel.selectTone("shorter")
        assertEquals("shorter", viewModel.uiState.value.selectedTone)

        viewModel.selectTone("maxVariance")
        assertEquals("maxVariance", viewModel.uiState.value.selectedTone)
    }
}
