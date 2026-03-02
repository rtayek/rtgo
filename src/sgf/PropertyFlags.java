package sgf;

import java.util.List;

final class PropertyFlags {
    static final class Flags {
        final boolean hasAMove;
        final boolean hasAMoveType;
        final boolean hasASetupType;

        Flags(boolean hasAMove,boolean hasAMoveType,boolean hasASetupType) {
            this.hasAMove=hasAMove;
            this.hasAMoveType=hasAMoveType;
            this.hasASetupType=hasASetupType;
        }
    }

    static Flags analyze(List<SgfProperty> sgfProperties) {
        boolean hasAMove=false;
        boolean hasAMoveType=false;
        boolean hasASetupType=false;
        for(SgfProperty property:sgfProperties) {
            if(property.p() instanceof Setup) hasASetupType=true;
            if(property.p() instanceof Move) hasAMoveType=true;
            if(property.p().equals(P.W)||property.p().equals(P.B)) hasAMove=true;
        }
        return new Flags(hasAMove,hasAMoveType,hasASetupType);
    }

    static boolean hasMixedMoveAndSetup(boolean hasAMoveType,boolean hasASetupType) {
        return hasAMoveType&&hasASetupType;
    }

    private PropertyFlags() {}
}
