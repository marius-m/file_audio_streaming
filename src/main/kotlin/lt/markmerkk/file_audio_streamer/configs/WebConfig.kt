package lt.markmerkk.file_audio_streamer.configs

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.ViewResolver
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.thymeleaf.TemplateEngine
import org.thymeleaf.spring5.SpringTemplateEngine
import org.thymeleaf.spring5.view.ThymeleafViewResolver
import org.thymeleaf.templateresolver.ServletContextTemplateResolver


@Configuration
@EnableWebMvc
@ComponentScan("lt.markmerkk")
open class WebConfig : WebMvcConfigurerAdapter() {

    @Bean
    open fun viewResolver(templateEngine: TemplateEngine): ViewResolver {
        val resolver = ThymeleafViewResolver()
        resolver.templateEngine = templateEngine as SpringTemplateEngine
        return resolver
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        super.addResourceHandlers(registry)
        registry.addResourceHandler(*arrayOf("/assets/**"))
                .addResourceLocations(*arrayOf("classpath:/assets/"))
        registry.addResourceHandler(*arrayOf("/static/**"))
                .addResourceLocations(*arrayOf("classpath:/static/"))
    }

}
