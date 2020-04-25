package io.ruban.service

import io.ruban.repository.Repository
import org.junit.Assert
import kotlin.test.BeforeTest
import kotlin.test.Test

class DataAggregatorTest {

    private lateinit var repository: Repository
    private lateinit var unit: DataAggregator

    @BeforeTest
    fun `before each`() {
        repository = Repository()
        unit = DataAggregator(repository)
    }

    @Test
    fun `should list all active instruments`() {
        (1..100).forEach {
            repository.activate(isin = "isin-$it", description = "isin-$it-active")
        }

        val list1 = unit.list()
        Assert.assertEquals(100, list1.size)

        (1..30).forEach {
            repository.disable(isin = "isin-$it", description = "isin-$it-active")
        }

        val list2 = unit.list()
        Assert.assertEquals(70, list2.size)
    }
}