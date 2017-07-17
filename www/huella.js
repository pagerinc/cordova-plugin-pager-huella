var exec = require('cordova/exec');

function Huella() {
}

Huella.prototype.isAvailable = function() {
    return new Promise((resolve, reject) => {
      exec(resolve, reject, "Huella", "isAvailable", []);
    });
};

Huella.prototype.showUnlock = function() {
    return new Promise((resolve, reject) => {
      exec(resolve, reject, "Huella", "showUnlock", []);
    });
};

module.exports = new Huella();
