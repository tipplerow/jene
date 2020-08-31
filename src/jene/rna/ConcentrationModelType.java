
package jene.rna;

/**
 * Enumerates types of protein concentration models.
 */
public enum ConcentrationModelType {
    /**
     * The protein concentration is equal to the expression level:
     *
     * <pre>
     *     C = min(FPKM, Cmax),
     * </pre>
     *
     * where {@code FPKM} is the RNA transcript expression level and
     * {@code Cmax} is the maximum concentration.
     */
    LINEAR {
        @Override public ConcentrationModel defaultModel() {
            return LinearConcentrationModel.DEFAULT;
        }

        @Override public ConcentrationModel globalModel() {
            return LinearConcentrationModel.global();
        }
    },

    /**
     * The protein concentration is equal to:
     *
     * <pre>
     *     C = min[log(1 + FPKM / alpha), Cmax],
     * </pre>
     *
     * where {@code FPKM} is the RNA transcript expression level,
     * {@code alpha} is a scale parameter (in units of FPKM), and
     * {@code Cmax} is the maximum (log-transformed) concentration.
     */
    LOG {
        @Override public ConcentrationModel defaultModel() {
            return LogConcentrationModel.DEFAULT;
        }

        @Override public ConcentrationModel globalModel() {
            return LogConcentrationModel.global();
        }
    },

    /**
     * The protein concentration, {@code C}, is a step function:
     *
     * <pre>
     *     C = 0, FPKM &lt; Fmin,
     *     C = 1, FPKM &ge; Fmin.
     * </pre>
     *
     * where {@code FPKM} is the RNA transcript expression level and
     * {@code Fmin} is a threshold expression level.
     */
    STEP {
        @Override public ConcentrationModel defaultModel() {
            return StepConcentrationModel.DEFAULT;
        }

        @Override public ConcentrationModel globalModel() {
            return StepConcentrationModel.global();
        }
    };

    /**
     * Returns the default model of this type (with parameters
     * assigned to their default values).
     *
     * @return the default model of this type.
     */
    public abstract ConcentrationModel defaultModel();

    /**
     * Returns the global model of this type (with parameters
     * specified by global system properties).
     *
     * @return the global model of this type.
     */
    public abstract ConcentrationModel globalModel();
}
