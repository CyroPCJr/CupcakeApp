package com.example.cupcake.test

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.cupcake.R
import com.example.cupcake.data.DataSource
import com.example.cupcake.data.OrderUiState
import com.example.cupcake.ui.OrderSummaryScreen
import com.example.cupcake.ui.SelectOptionScreen
import com.example.cupcake.ui.StartOrderScreen
import org.junit.Rule
import org.junit.Test

/*
    Verify the Start screen content.
    Verify the Summary screen content.
    Verify that the Next button is enabled when an option is selected on the Choose Flavor screen.
* */

class AndroidComposeTestRule {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun startOrderScreen_verifyContent() {
        composeTestRule.setContent {
            StartOrderScreen(DataSource.quantityOptions, {})
        }

        // Then all the options are displayed on the screen.
        DataSource.quantityOptions.forEach { flavor ->
            composeTestRule.onNodeWithStringId(flavor.first).assertIsDisplayed()
        }
    }

    @Test
    fun summaryScreen_verifyContent() {
        val order = OrderUiState(
            1,
            DataSource.flavors[1].toString(),
            "Jan 1",
            "$100",
            listOf()
        )
        composeTestRule.apply {
            setContent {
                OrderSummaryScreen(
                    orderUiState = order,
                    onCancelButtonClicked = {},
                    onSendButtonClicked = { _, _ -> },
                )
            }

            val quantity = activity.resources.getQuantityString(
                R.plurals.cupcakes,
                order.quantity,
                order.quantity
            )
            val subTotal = activity.getString(
                R.string.subtotal_price,
                order.price
            )
            onNodeWithText(quantity).assertIsDisplayed()
            onNodeWithText(order.flavor).assertIsDisplayed()
            onNodeWithText(order.date).assertIsDisplayed()
            onNodeWithText(subTotal).assertIsDisplayed()
        }
    }

    @Test
    fun selectOptionScreen_optionsSelected_NextButtonEnabled() {
        val listFlavors: List<String> = DataSource.flavors.map { it.toString() }

        composeTestRule.setContent {
            SelectOptionScreen(options = listFlavors, subtotal = "$100")
        }

        listFlavors.forEach { flavor ->
            composeTestRule.onNodeWithText(flavor).assertIsDisplayed()
        }

        composeTestRule.apply {
            onNodeWithText(listFlavors[0]).performClick()
            onNodeWithStringId(R.string.next).assertIsDisplayed()
        }
    }

    @Test
    fun selectOptionScreen_verifyContent() {
        // Given list of options
        val flavors = DataSource.flavors.map { it.toString() }
        // And subtotal
        val subtotal = "$100"

        // When SelectOptionScreen is loaded
        composeTestRule.setContent {
            SelectOptionScreen(subtotal = subtotal, options = flavors)
        }

        // Then all the options are displayed on the screen.
        flavors.forEach { flavor ->
            composeTestRule.onNodeWithText(flavor).assertIsDisplayed()
        }

        // And then the subtotal is displayed correctly.
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(
                R.string.subtotal_price,
                subtotal
            )
        ).assertIsDisplayed()

        // And then the next button is disabled
        composeTestRule.onNodeWithStringId(R.string.next).assertIsNotEnabled()
    }
}