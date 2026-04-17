package com.example.Usermangement.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.Usermangement.Bean.ServiceItem;
import com.example.Usermangement.Repository.ServiceItemRepository;

@Component
public class ServiceCatalogSeeder implements CommandLineRunner {

    private final ServiceItemRepository serviceItemRepository;

    public ServiceCatalogSeeder(ServiceItemRepository serviceItemRepository) {
        this.serviceItemRepository = serviceItemRepository;
    }

    @Override
    public void run(String... args) {
        if (serviceItemRepository.count() > 0) {
            return;
        }

        List<ServiceSeed> seeds = new ArrayList<>();
        seeds.add(new ServiceSeed("Private Limited Registration", "Entity Registrations", "Company incorporation service", "ENTITY-PRIVATE-LTD", "9999"));
        seeds.add(new ServiceSeed("Public Limited Registration", "Entity Registrations", "Company incorporation service", "ENTITY-PUBLIC-LTD", "14999"));
        seeds.add(new ServiceSeed("One Person Company (OPC) Registration", "Entity Registrations", "Single promoter company registration", "ENTITY-OPC", "8999"));
        seeds.add(new ServiceSeed("Farmer Producer Company (FPO) Registration", "Entity Registrations", "FPO incorporation and registration", "ENTITY-FPO", "11999"));
        seeds.add(new ServiceSeed("Limited Liability Partnership (LLP) Registration", "Entity Registrations", "LLP incorporation service", "ENTITY-LLP", "7499"));
        seeds.add(new ServiceSeed("Section 8 Company (NGO) Registration", "Entity Registrations", "Non-profit company registration", "ENTITY-SECTION8", "12999"));
        seeds.add(new ServiceSeed("Nidhi Company Registration", "Entity Registrations", "Nidhi company setup", "ENTITY-NIDHI", "13999"));
        seeds.add(new ServiceSeed("Trust Registration", "Entity Registrations", "Trust deed and registration support", "ENTITY-TRUST", "6999"));
        seeds.add(new ServiceSeed("Society Registration", "Entity Registrations", "Society registration support", "ENTITY-SOCIETY", "6499"));
        seeds.add(new ServiceSeed("Partnership Firm Registration", "Entity Registrations", "Partnership deed and registration", "ENTITY-PARTNERSHIP", "4999"));

        seeds.add(new ServiceSeed("GST Registration", "Licenses & Registrations", "GSTIN registration filing", "LICENSE-GST-REG", "1999"));
        seeds.add(new ServiceSeed("TAN Application", "Licenses & Registrations", "TAN allotment support", "LICENSE-TAN", "1499"));
        seeds.add(new ServiceSeed("Food License (FSSAI)", "Licenses & Registrations", "FSSAI registration filing", "LICENSE-FSSAI", "2499"));
        seeds.add(new ServiceSeed("Startup Registration", "Licenses & Registrations", "Startup registration support", "LICENSE-STARTUP", "2499"));
        seeds.add(new ServiceSeed("Udyam Aadhar (MSME) Registration", "Licenses & Registrations", "MSME registration filing", "LICENSE-UDYAM", "1499"));
        seeds.add(new ServiceSeed("Import Export Code (IEC)", "Licenses & Registrations", "IEC registration support", "LICENSE-IEC", "1999"));
        seeds.add(new ServiceSeed("ICEGATE / DSC Registration", "Licenses & Registrations", "ICEGATE and DSC setup", "LICENSE-ICEGATE-DSC", "2499"));
        seeds.add(new ServiceSeed("Digital Signature", "Licenses & Registrations", "Digital signature certificate service", "LICENSE-DSC", "999"));
        seeds.add(new ServiceSeed("Shop and Establishment Registration", "Licenses & Registrations", "Shops act registration", "LICENSE-SHOP", "1499"));
        seeds.add(new ServiceSeed("12A and 80G Registration", "Licenses & Registrations", "NGO tax exemption registration", "LICENSE-12A-80G", "4999"));
        seeds.add(new ServiceSeed("PF/ESI Registration", "Licenses & Registrations", "PF and ESI registration support", "LICENSE-PF-ESI", "2499"));
        seeds.add(new ServiceSeed("Trademark Registration", "Licenses & Registrations", "Trademark filing service", "LICENSE-TM", "6999"));
        seeds.add(new ServiceSeed("ISO Certificate", "Licenses & Registrations", "ISO certification documentation support", "LICENSE-ISO", "7999"));
        seeds.add(new ServiceSeed("Pollution NOC", "Licenses & Registrations", "Pollution NOC application service", "LICENSE-POLLUTION-NOC", "5499"));
        seeds.add(new ServiceSeed("FCRA Registration", "Licenses & Registrations", "FCRA registration support", "LICENSE-FCRA", "9999"));
        seeds.add(new ServiceSeed("DPIIT Startup Recognition & Tax Exemption", "Licenses & Registrations", "DPIIT and tax exemption support", "LICENSE-DPIIT", "3999"));

        seeds.add(new ServiceSeed("ITR without Capital Gain", "Income Tax Return", "Annual return filing", "ITR-WITHOUT-CG", "999"));
        seeds.add(new ServiceSeed("ITR with Capital Gain", "Income Tax Return", "Return filing including capital gains", "ITR-WITH-CG", "1999"));
        seeds.add(new ServiceSeed("NRI / Foreign Income ITR", "Income Tax Return", "NRI and foreign income return filing", "ITR-NRI-FOREIGN", "2999"));

        seeds.add(new ServiceSeed("GST Return Filing", "Annual Compliances", "Monthly/quarterly GST return filing", "COMPLIANCE-GST-RETURN", "1499"));
        seeds.add(new ServiceSeed("ROC Annual Filing", "Annual Compliances", "ROC annual filing support", "COMPLIANCE-ROC-ANNUAL", "3999"));
        seeds.add(new ServiceSeed("Monthly PF/ESI Return Filing", "Annual Compliances", "PF/ESI monthly return filing", "COMPLIANCE-PF-ESI-MONTHLY", "1999"));

        seeds.add(new ServiceSeed("Detailed Project Report", "Subsidy / Funding", "Project report preparation", "FUNDING-DPR", "4999"));
        seeds.add(new ServiceSeed("CMA Data Preparation", "Subsidy / Funding", "CMA data for funding proposals", "FUNDING-CMA", "7999"));
        seeds.add(new ServiceSeed("Government Subsidy Assistance", "Subsidy / Funding", "Subsidy application support", "FUNDING-GOVT-SUBSIDY", "5999"));
        seeds.add(new ServiceSeed("Government Funding Assistance", "Subsidy / Funding", "Funding application support", "FUNDING-GOVT", "6999"));
        seeds.add(new ServiceSeed("Startup / Seed Funding Assistance", "Subsidy / Funding", "Pitch and funding support", "FUNDING-STARTUP-SEED", "9999"));

        seeds.add(new ServiceSeed("Accounting Package 1-25 Invoices/Month", "Accounting", "Virtual accounting support", "ACC-1-25", "500"));
        seeds.add(new ServiceSeed("Accounting Package 25-50 Invoices/Month", "Accounting", "Virtual accounting support", "ACC-25-50", "1000"));
        seeds.add(new ServiceSeed("Accounting Package 50-100 Invoices/Month", "Accounting", "Virtual accounting support", "ACC-50-100", "1500"));
        seeds.add(new ServiceSeed("Accounting Package 100-150 Invoices/Month", "Accounting", "Virtual accounting support", "ACC-100-150", "2000"));
        seeds.add(new ServiceSeed("Accounting Package 150-200 Invoices/Month", "Accounting", "Virtual accounting support", "ACC-150-200", "2500"));

        LocalDateTime now = LocalDateTime.now();
        for (ServiceSeed seed : seeds) {
            ServiceItem item = new ServiceItem();
            item.setCode(seed.code());
            item.setName(seed.name());
            item.setDescription(seed.category() + " - " + seed.description());
            item.setPrice(new BigDecimal(seed.price()));
            item.setActive(true);
            item.setCreatedAt(now);
            item.setUpdatedAt(now);
            serviceItemRepository.save(item);
        }
    }

    private record ServiceSeed(String name, String category, String description, String code, String price) {
    }
}
