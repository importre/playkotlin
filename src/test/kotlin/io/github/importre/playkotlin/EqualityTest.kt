package io.github.importre.playkotlin

import org.junit.Test
import rx.Observable
import rx.schedulers.Schedulers
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class EqualityTest {

    // -----------------------------------------------------------------------
    // User
    class User(val name: String)

    data class UserData(val name: String)

    @Test
    fun testUser() {
        val u1 = User("heo")
        val u2 = User("heo")
        assertNotEquals(u1, u2)
        u1 === u2
    }

    @Test
    fun testUserData() {
        val u1 = UserData("heo")
        val u2 = UserData("heo")
        assertEquals(u1, u2)
    }

    // -----------------------------------------------------------------------
    // User with List
    class UserWithList(val name: String, val emails: List<String>)

    data class UserDataWithList(val name: String, val emails: List<String>)

    @Test
    fun testList() {
        // list
        assertEquals(listOf("a@b.com", "c@d.com"), 
                listOf("a@b.com", "c@d.com"))
        // nested list
        assertEquals(listOf(listOf("a@b.com"), listOf("c@d.com")),
                listOf(listOf("a@b.com"), listOf("c@d.com")))
    }

    @Test
    fun testUserWithList() {
        val u1 = UserWithList("heo", listOf("a@b.com", "c@d.com"))
        val u2 = UserWithList("heo", listOf("a@b.com", "c@d.com"))
        assertNotEquals(u1, u2)
    }

    @Test
    fun testUserDataWithList() {
        val u1 = UserDataWithList("heo", listOf("a@b.com", "c@d.com"))
        val u2 = UserDataWithList("heo", listOf("a@b.com", "c@d.com"))
        assertEquals(u1, u2)
    }

    class UserWithMap(val name: String, val emails: Map<Int, String>)
    data class UserDataWithMap(val name: String, val emails: Map<Int, String>)

    // -----------------------------------------------------------------------
    // User with Map
    @Test
    fun testUserWithMap() {
        val u1 = UserWithMap("heo", mapOf(0 to "a@b.com", 1 to "c@d.com"))
        val u2 = UserWithMap("heo", mapOf(0 to "a@b.com", 1 to "c@d.com"))
        assertNotEquals(u1, u2)
    }

    @Test
    fun testUserDataWithMap() {
        val u1 = UserDataWithMap("heo", mapOf(0 to "a@b.com", 1 to "c@d.com"))
        val u2 = UserDataWithMap("heo", mapOf(1 to "c@d.com", 0 to "a@b.com"))
        assertEquals(u1, u2)
    }

    // -----------------------------------------------------------------------
    // User with Array
    class UserWithArray(val name: String, val emails: Array<String>)

    data class UserDataWithArray(val name: String, val emails: Array<String>)

    @Test
    fun testUserWithArray() {
        val a1 = arrayOf("a@b.com", "c@d.com")
        val a2 = arrayOf("a@b.com", "c@d.com")
        assertNotEquals(a1, a2)

        val u1 = UserWithArray("heo", a1)
        val u2 = UserWithArray("heo", a2)
        assertNotEquals(u1, u2)
    }

    @Test
    fun testUserDataWithArray() {
        val u1 = UserDataWithArray("heo", arrayOf("a@b.com", "c@d.com"))
        val u2 = UserDataWithArray("heo", arrayOf("a@b.com", "c@d.com"))
        assertNotEquals(u1, u2)
    }

    // -----------------------------------------------------------------------
    // Rx - distinct
    @Test
    fun testWithRx() {
        val users = arrayOf(
                UserDataWithList("heo", listOf("a@b.com", "c@d.com")),
                UserDataWithList("heo", listOf("a@b.com", "c@d.com")),
                UserDataWithList("kim", listOf("a@b.com", "c@d.com")),
                UserDataWithList("heo", listOf("a@b.com", "c@d.com")),
                UserDataWithList("heo", listOf("a@b.com", "c@d.com"))
        )

        val count = CountDownLatch(users.size)
        Observable.from(users)
                .distinctUntilChanged()
                .observeOn(Schedulers.newThread())
                .subscribe {
                    count.countDown()
                    println(it)
                }

        count.await(1, TimeUnit.SECONDS)
        assertEquals(2, count.count) // 2개가 남아야 함
    }
}
