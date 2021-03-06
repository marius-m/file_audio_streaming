package lt.markmerkk.file_audio_streamer.configs

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


@Configuration
@EnableWebSecurity
@Profile("prod")
class SecurityConfigProd : WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var credentials: Credentials

    override fun configure(auth: AuthenticationManagerBuilder) {
        l.info("Using PROD security")
        if (!credentials.isEmpty()) {
            auth.inMemoryAuthentication()
                    .withUser(credentials.username)
                    .password(passwordEncoder().encode(credentials.password))
                    .roles("USER")
        }
    }

    override fun configure(httpSec: HttpSecurity) {
        if (!credentials.isEmpty()) {
            httpSec.authorizeRequests()
                    .anyRequest()
                    .authenticated()
                    .and()
                    .httpBasic()
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    companion object {
        private val l = LoggerFactory.getLogger(SecurityConfigDev::class.java)!!
    }

}