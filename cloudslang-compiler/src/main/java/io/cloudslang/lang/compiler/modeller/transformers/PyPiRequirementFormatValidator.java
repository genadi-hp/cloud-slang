package io.cloudslang.lang.compiler.modeller.transformers;

import org.springframework.stereotype.Component;

/**
 * Created by Genadi Rabinovich, genadi@hpe.com on 23/06/2016.
 */
@Component
class PyPiRequirementFormatValidator {
    private static final String STRREQUIREMENT_EQ = "==";

    public static final String INVALID_REQUIREMENT = "Requirement definition should match pattern 'library_name==library_version', got:";

    void validatePyPiRequirement(String requirement) {
        if(!isPipRequirement(requirement) ||
                (getLibraryNameFromRequirement(requirement) == null) ||
                (getLibraryVersionFromRequirement(requirement) == null)) {
            throw new RuntimeException(INVALID_REQUIREMENT + requirement);
        }
    }

    String normalizedRequirement(String requirement) {
        return getLibraryNameFromRequirement(requirement) + STRREQUIREMENT_EQ + getLibraryVersionFromRequirement(requirement);
    }

    private boolean isPipRequirement(String requirement) {
        return requirement.contains(STRREQUIREMENT_EQ);
    }

    private String getLibraryNameFromRequirement(String requirement) {
        String processStr = requirement.trim();
        int index = processStr.indexOf(STRREQUIREMENT_EQ);
        if(index > 0) {
            return processStr.substring(0, index).trim();
        }
        return null;
    }

    private String getLibraryVersionFromRequirement(String requirement) {
        String processStr = requirement.trim();
        int index = processStr.indexOf(STRREQUIREMENT_EQ);
        if((index > -1) && (processStr.length() > (index + STRREQUIREMENT_EQ.length()))) {
            return processStr.substring(index + STRREQUIREMENT_EQ.length()).trim();
        }
        return null;
    }
}
