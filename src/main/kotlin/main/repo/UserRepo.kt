package main.repo

import main.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepo : JpaRepository<User, Long> {
    fun findByUsername(username: String):User?
}