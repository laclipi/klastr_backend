package com.klastr.klastrbackend.domain.internship.attendance;

public enum WeekStatus {

    OPEN {
        @Override
        public WeekStatus submit() {
            return SUBMITTED;
        }

        @Override
        public boolean isEditable() {
            return true;
        }
    },

    SUBMITTED {
        @Override
        public WeekStatus approve() {
            return APPROVED;
        }

        @Override
        public WeekStatus reject() {
            return REJECTED;
        }
    },

    APPROVED,

    REJECTED {
        @Override
        public boolean isEditable() {
            return true;
        }
    };

    // -----------------------------
    // Default invalid transitions
    // -----------------------------

    public WeekStatus submit() {
        throw invalidTransition("submit");
    }

    public WeekStatus approve() {
        throw invalidTransition("approve");
    }

    public WeekStatus reject() {
        throw invalidTransition("reject");
    }

    public boolean isEditable() {
        return false;
    }

    protected IllegalStateException invalidTransition(String action) {
        return new IllegalStateException(
                "Cannot " + action + " when status is " + this);
    }
}