
## =====================================================================
## R source code for processing supplemental data from Miao et al.,
## "Genomic correlates of response to immune checkpoint blockade in
## microsatellite-stable solid tumors", Nat. Gen. 50, 1271 (2018).
## =====================================================================

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

## =====================================================================

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
                   PFS_Days      = detailFrame$pfs_days,
                   RECIST        = detailFrame$RECIST,
                   VA            = detailFrame$va_response,
                   ROH           = detailFrame$roh_response)

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

## ---------------------------------------------------------------------

Miao.buildIndex <- function() {
    detailFrame <- Miao.loadPatientDetail()

    indexFrame <-
        data.frame(pair_id = detailFrame$pair_id,
                   patient_id = detailFrame$patient_id,
                   Patient_ID = detailFrame$patient_id,
                   Tumor_Barcode = detailFrame$Tumor_Sample_Barcode,
                   Tumor_Sample_Barcode = detailFrame$Tumor_Sample_Barcode)

    indexFrame
}

## ---------------------------------------------------------------------

Miao.cancerCodeMap <- function() {
    data.frame(Miao_Cancer_Type = c("Bladder", "HNSCC", "Lung", "Melanoma"),
               TCGA_Cancer_Type = c("BLCA",    "HNSC",  "LUAD", "SKCM"))
}

## ---------------------------------------------------------------------

Miao.computeTMB <- function(mutDetail) {
    aggFunc <- function(slice) {
        data.frame(pair_id = slice$pair_id[1],
                   missenseCount = sum(slice$Variant_Classification == "Missense_Mutation"),
                   nonSilentCount = sum(slice$Variant_Classification %in%
                                        c("Nonstop_Mutation",
                                          "In_Frame_Ins",
                                          "In_Frame_Del",
                                          "Frame_Shift_Ins",
                                          "Frame_Shift_Del",
                                          "Missense_Mutation")))
    }

    result <- do.call(rbind, by(mutDetail, mutDetail$pair_id, aggFunc))
    rownames(result) <- NULL

    index  <- Miao.buildIndex()
    result <- merge(result, index, by = "pair_id")
    result <- result[,c("Tumor_Barcode", "missenseCount", "nonSilentCount")]
    result
}

Miao.writeTMB <- function(tmb) {
    write.csv(tmb, file.path(Miao.homeDir(), "Cohort", "Miao_TMB.csv"), quote = FALSE, row.names = FALSE)
}

## ---------------------------------------------------------------------

Miao.loadMutDetail <- function() {
    JamIO.load(file.path(Miao.rawDir(), "Miao_SupTable5.RData"))
}

Miao.loadNeoDetail <- function() {
    JamIO.load(file.path(Miao.rawDir(), "Miao_SupTable10.RData"))
}

Miao.loadPatientDetail <- function() {
    read.csv(file.path(Miao.rawDir(), "Miao_SupTable2.csv"))
}

Miao.loadTMB <- function() {
    read.csv(file.path(Miao.homeDir(), "Cohort", "Miao_TMB.csv"))
}

## ---------------------------------------------------------------------

Miao.pairRN <- function() {
    dframe <- Miao.buildCoxModelFrame()
    dframe <- merge(dframe, Miao.loadTMB(), by = "Tumor_Barcode")
    dframe <- dframe[order(dframe$Cancer_Type, dframe$Drug_Type, dframe$missenseCount),]

    dframe$Responder <-
        dframe$ROH == "clinical benefit"

    pairList <- list()

    for (k2 in 2:nrow(dframe)) {
        k1 <- k2 - 1

        if (dframe$Cancer_Type[k1] != dframe$Cancer_Type[k2])
            next

        if (dframe$Drug_Type[k1] != dframe$Drug_Type[k2])
            next

        if (dframe$Responder[k1] == dframe$Responder[k2])
            next

        if (dframe$Responder[k1]) {
            res <- k1
            non <- k2
        }
        else {
            res <- k2
            non <- k1
        }

        stopifnot(isTRUE(dframe$Responder[res]))
        stopifnot(isFALSE(dframe$Responder[non]))

        pairRow <-
            data.frame(Tumor_Barcode.Responder = dframe$Tumor_Barcode[res],
                       Tumor_Barcode.NonResponder = dframe$Tumor_Barcode[non])

        pairList[[length(pairList) + 1]] <- pairRow
    }

    pairFrame <- do.call(rbind, pairList)
    pairFrame
}

## ---------------------------------------------------------------------
