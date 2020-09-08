
## ---------------------------------------------------------------------
## R source code for processing supplemental data from Miao et al.,
## "Genomic correlates of response to immune checkpoint blockade in
## microsatellite-stable solid tumors", Nat. Gen. 50, 1271 (2018).
## ---------------------------------------------------------------------

Miao.dataVault <- function() {
    dataVault <- Sys.getenv("TIPPLEROW_DATA_VAULT", unset = NA)

    if (is.na(dataVault))
        JamLog.error("Environment variable TIPPLEROW_DATA_VAULT is not set.");

    dataVault
}

Miao.homeDir <- function() {
    file.path(Miao.dataVault(), "Miao")
}

Miao.rawDir <- function() {
    file.path(Miao.homeDir(), "Raw")
}

## ---------------------------------------------------------------------

Miao.cancerCodeMap <- function() {
    data.frame(Miao_Cancer_Type = c("Bladder", "HNSCC", "Lung", "Melanoma"),
               TCGA_Cancer_Type = c("BLCA",    "HNSC",  "LUAD", "SKCM"))
}

## ---------------------------------------------------------------------

Miao.loadPatientDetail <- function() {
    read.csv(file.path(Miao.rawDir(), "Miao_SupTable2.csv"))
}

## ---------------------------------------------------------------------

Miao.buildCoxModelFrame <- function() {
    detailFrame <- Miao.loadPatientDetail()
    detailFrame <- subset(detailFrame, cancer_type %in% c("Bladder", "HNSCC", "Lung", "Melanoma"))

    indexFrame <-
        data.frame(cancer_type  = c("Bladder", "HNSCC", "Lung", "Melanoma"),
                   Cancer_Index = 1:4)

    detailFrame <- merge(detailFrame, indexFrame)

    modelFrame <- 
        data.frame(Patient_ID    = detailFrame$patient_id,
                   Tumor_Barcode = detailFrame$Tumor_Sample_Barcode,
                   Cancer_Type   = detailFrame$cancer_type,
                   Cancer_Index  = detailFrame$Cancer_Index,
                   Drug_Type     = detailFrame$drug_type,
                   OS_Days       = detailFrame$os_days,
                   PFS_Days      = detailFrame$pfs_days)

    ## Death occurred if the overall survival is censored...
    modelFrame$OS_Event <- 1L - detailFrame$os_censor

    ## Progression occurred if the PFS duration is shorter than the overall duration...
    modelFrame$PFS_Event <- as.numeric(modelFrame$PFS_Days < modelFrame$OS_Days)

    ## Melanoma will be the reference...
    modelFrame$Bladder  <- as.numeric(modelFrame$Cancer_Type == "Bladder")
    modelFrame$HNSCC    <- as.numeric(modelFrame$Cancer_Type == "HNSCC")
    modelFrame$Lung     <- as.numeric(modelFrame$Cancer_Type == "Lung")
    modelFrame$Melanoma <- as.numeric(modelFrame$Cancer_Type == "Melanoma")

    ## "Anti-CTLA-4" will be the reference...
    modelFrame$CTLA4 <- as.numeric(modelFrame$Drug_Type == "anti-CTLA-4")
    modelFrame$PD1   <- as.numeric(modelFrame$Drug_Type == "anti-PD-1/anti-PD-L1")
    modelFrame$Both  <- as.numeric(modelFrame$Drug_Type == "anti-CTLA-4 + anti-PD-1/PD-L1")

    modelFrame
}
