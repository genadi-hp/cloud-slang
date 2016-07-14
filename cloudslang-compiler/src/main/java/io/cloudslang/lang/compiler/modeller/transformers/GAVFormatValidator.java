package io.cloudslang.lang.compiler.modeller.transformers;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Created by Genadi Rabinovich, genadi@hpe.com on 23/06/2016.
 */
@Component
class GAVFormatValidator {
    private static final int GAV_PARTS = 3;
    public static final String INVALID_GAV = "GAV definition should contain exactly [" + GAV_PARTS + "] non empty parts separated by ':'";
    void validateGAV(String gav) {
        String [] gavParts = gav.split(":");
        if(gavParts.length != GAV_PARTS ||
                StringUtils.isEmpty(gavParts[0].trim()) ||
                StringUtils.isEmpty(gavParts[1].trim()) ||
                StringUtils.isEmpty(gavParts[2].trim())) {
            throw new RuntimeException(INVALID_GAV);
        }
    }
}
