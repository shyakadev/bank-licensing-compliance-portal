package gov.bnr.licensing_compliance_service;

import org.springframework.boot.SpringApplication;

public class TestLicensingComplianceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(LicensingComplianceServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
