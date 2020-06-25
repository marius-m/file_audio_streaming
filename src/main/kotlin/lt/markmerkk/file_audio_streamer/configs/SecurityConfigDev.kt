package lt.markmerkk.file_audio_streamer.configs

import lt.markmerkk.file_audio_streamer.controllers.HomeController
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
@Profile("dev")
class SecurityConfigDev : WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var credentials: Credentials

    override fun configure(auth: AuthenticationManagerBuilder) {
        l.info("Using DEV security")
    }

    override fun configure(httpSecurity: HttpSecurity) {
        httpSecurity.authorizeRequests().antMatchers("/").permitAll()
                .and()
                .authorizeRequests().antMatchers("/console/**").permitAll()
        httpSecurity.csrf().disable()
        httpSecurity.headers().frameOptions().disable()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    companion object {
        private val l = LoggerFactory.getLogger(SecurityConfigDev::class.java)!!
    }

}