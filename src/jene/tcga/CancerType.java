
package jene.tcga;

/**
 * Enumerates the distinct cancer types identified in the TCGA database.
 */
public enum CancerType {
    ACC( "Adrenocortical carcinoma"),
    BLCA("Bladder urothelial carcinoma"),
    BRCA("Breast invasive carcinoma"),
    CESC("Cervical squamous cell carcinoma and endocervical adenocarcinoma"),
    CHOL("Cholangiocarcinoma"),
    COAD("Colon adenocarcinoma"),
    DLBC(""),
    ESCA("Esophageal carcinoma"),
    GBM( "Glioblastoma multiforme"),
    HNSC("Head and Neck squamous cell carcinoma"),
    KICH("Kidney chromophobe"),
    KIRC("Kidney renal clear cell carcinoma"),
    KIRP("Kidney renal papillary cell carcinoma"),
    LAML("Acute myeloid leukemia"),
    LGG( "Brain lower grade glioma"),
    LIHC("Liver hepatocellular carcinoma"),
    LUAD("Lung adenocarcinoma"),
    LUSC("Lung squamous cell carcinoma"),
    MESO("Mesothelioma"),
    OV(  "Ovarian serous cystadenocarcinoma"),
    PAAD("Pancreatic adenocarcinoma"),
    PCPG("Pheochromocytoma and paraganglioma"),
    PRAD("Prostate adenocarcinoma"),
    READ("Rectum adenocarcinoma"),
    SARC("Sarcoma"),
    SKCM("Skin cutaneous melanoma"),
    STAD("Stomach adenocarcinoma"),
    TGCT("Testicular germ cell tumors"),
    THCA("Thyroid carcinoma"),
    THYM("Thymoma"),
    UCEC("Uterine corpus endometrial carcinoma"),
    UCS( "Uterine carcinosarcoma"),
    UVM( "Uveal melanoma");

    private final String longName;

    private CancerType(String longName) {
        this.longName = longName;
    }
}
