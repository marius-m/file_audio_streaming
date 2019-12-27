package lt.markmerkk.file_audio_streamer.configs

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
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
        println("Using credentials: $credentials")
        if (!credentials.isEmpty()) {
            auth.inMemoryAuthentication()
                    .withUser(credentials.username)
                    .password(passwordEncoder().encode(credentials.password))
                    .roles("USER")
        }
    }

    override fun configure(http: HttpSecurity) {
        if (!credentials.isEmpty()) {
            http.authorizeRequests()
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

}