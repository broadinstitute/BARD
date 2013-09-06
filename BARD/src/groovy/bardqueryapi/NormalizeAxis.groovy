package bardqueryapi

/**
 *
 * An enum for toggling normalization and denormalization of Y-Axis in curve plots
 */
enum NormalizeAxis {

    Y_NORM_AXIS('Normalize Y-Axis'),


    Y_DENORM_AXIS('DeNormalize Y-Axis');

    private final String label;

    private NormalizeAxis(String label) {
        this.label = label;
    }
    /**
     * @return the label
     */
    String getLabel() {
        return this.label;
    }
}