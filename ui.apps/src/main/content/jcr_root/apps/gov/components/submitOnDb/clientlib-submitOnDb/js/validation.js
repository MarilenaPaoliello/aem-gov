(function (window, $, Granite, Coral) {

    "use strict";

    var ACN_TEXT_MAX_LENGTH_4 = "acn-text-max-length-4";

    var registry = $(window).adaptTo("foundation-registry");

    registry.register("foundation.validation.validator", {

        selector: "[data-foundation-validation~='" + NUMBER_LESS_THAN_24 + "']",

        validate: function(element) {

            if (isNaN(element.value) ) {

                return "Only a number less than 24 is allowed";

            }

            if (element.value >= 24 ) {

                return "A value less than 24 is expected";

            }

        }

    })

})(window, Granite.$, Granite, Coral);