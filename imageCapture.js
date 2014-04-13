/**
 * A simple script for capturing an image of a webpage and saving it in .png format.
 */
var page = require('webpage').create();
page.viewportSize = { width: 1600, height: 1024 };
var system = require('system');

page.open(system.args[1], function() {
	var pageHeight = page.evaluate(function() {
		return document.body.offsetHeight;
	});
	
	page.render(system.args[2])
	phantom.exit();
});