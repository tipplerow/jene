
package jene.chem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jam.math.Probability;

/**
 * Implements the multicomponent Langmuir adsorption isotherm for a
 * fixed set of dimensionless concentrations.  
 */
public final class MultiLangmuir {
    private final Concentration total;
    private final List<Concentration> concs;
    
    /**
     * Creates a new multicomponent Langmuir adsorption isotherm for
     * a fixed set of dimensionless concentrations.
     *
     * <p>Unit equilibrium constants are assumed: each concentration
     * must be a dimensionless quantity equal to the product of the
     * bulk concentration and the equilibrium adsorption constant.
     *
     * @param concs the dimensionless concentrations of the chemical
     * species.
     */
    public MultiLangmuir(Concentration... concs) {
        this(Arrays.asList(concs));
    }

    /**
     * Creates a new multicomponent Langmuir adsorption isotherm for a
     * fixed set of concentrations.
     *
     * <p>Unit equilibrium constants are assumed: each concentration
     * must be a dimensionless quantity equal to the product of the
     * bulk concentration and the equilibrium adsorption constant.
     *
     * @param concs the dimensionless concentrations of the chemical
     * species.
     */
    public MultiLangmuir(List<Concentration> concs) {
        this.total = Concentration.total(concs);
        this.concs = new ArrayList<Concentration>(concs);
    }

    /**
     * Evaluates the adsorption isotherm for a given species.
     *
     * @param index the index of the species, corresponding to its
     * position in the collection of concentrations supplied to the
     * constructor.
     *
     * @return the probability that a surface site is occupied by the
     * given species.
     *
     * @throws IndexOutOfBoundsException unless the index is valid.
     */
    public double evaluate(int index) {
        return concs.get(index).doubleValue() / (1.0 + total.doubleValue());
    }

    /**
     * Evaluates the adsorption isotherm for a given species and
     * returns the result as a probability.
     *
     * @param index the index of the species, corresponding to its
     * position in the collection of concentrations supplied to the
     * constructor.
     *
     * @return the probability that a surface site is occupied by the
     * given species.
     *
     * @throws IndexOutOfBoundsException unless the index is valid.
     */
    public Probability probability(int index) {
        return Probability.valueOf(evaluate(index));
    }

    /**
     * Returns the dimensionless concentration of a given species.
     *
     * @param index the index of the species, corresponding to its
     * position in the collection of concentrations supplied to the
     * constructor.
     *
     * @return the dimensionless concentration of the given species.
     *
     * @throws IndexOutOfBoundsException unless the index is valid.
     */
    public Concentration getConcentration(int index) {
        return concs.get(index);
    }

    /**
     * Returns the dimensionless concentrations of the species modeled
     * by this isotherm.
     *
     * @return the dimensionless concentrations of the species modeled
     * by this isotherm.
     *
     * @throws IndexOutOfBoundsException unless the index is valid.
     */
    public List<Concentration> viewConcentrations() {
        return Collections.unmodifiableList(concs);
    }
}

