package com.statusdrafter.tool;

import com.statusdrafter.tool.entity.ParsingRule;
import com.statusdrafter.tool.entity.User;
import com.statusdrafter.tool.repository.ParsingRuleRepository;
import com.statusdrafter.tool.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class StatusDrafterApplication {
    public static void main(String[] args) {
        SpringApplication.run(StatusDrafterApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(UserRepository userRepo, ParsingRuleRepository ruleRepo, PasswordEncoder encoder) {
        return args -> {
            if (userRepo.count() == 0) {
                // Admin User
                User admin = new User();
                admin.setUsername("admin");
                admin.setFullName("System Admin");
                admin.setEmail("admin@status.com");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole("ROLE_ADMIN");
                userRepo.save(admin);

                // Normal Employee
                User user = new User(); 
                user.setUsername("jdoe");
                user.setFullName("John Doe");
                user.setEmail("john@example.com");
                user.setPassword(encoder.encode("user123"));
                user.setRole("ROLE_USER");
                userRepo.save(user);

                // Yesterday rules
                saveRule(ruleRepo, ParsingRule.Category.YESTERDAY, "worked on", 1);
                saveRule(ruleRepo, ParsingRule.Category.YESTERDAY, "completed", 2);
                saveRule(ruleRepo, ParsingRule.Category.YESTERDAY, "finished", 2);
                
                // Today rules
                saveRule(ruleRepo, ParsingRule.Category.TODAY, "plan to", 1);
                saveRule(ruleRepo, ParsingRule.Category.TODAY, "will do", 1);
                saveRule(ruleRepo, ParsingRule.Category.TODAY, "going to", 1);
                
                // Blocker rules
                saveRule(ruleRepo, ParsingRule.Category.BLOCKERS, "stuck", 3);
                saveRule(ruleRepo, ParsingRule.Category.BLOCKERS, "blocked", 3);
                saveRule(ruleRepo, ParsingRule.Category.BLOCKERS, "issue", 2);
            }
        };
    }

    private void saveRule(ParsingRuleRepository repo, ParsingRule.Category cat, String kw, int prio) {
        ParsingRule rule = new ParsingRule();
        rule.setCategory(cat);
        rule.setKeyword(kw);
        rule.setPriority(prio);
        repo.save(rule);
    }
}
