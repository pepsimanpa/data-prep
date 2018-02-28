const config = require('./webpack.config');
const webpack = require('webpack');

config.devtool = 'inline-source-map';
config.plugins.push(new webpack.DefinePlugin({
	'process.env.NODE_ENV': JSON.stringify('development'),
}));

config.watchOptions = {
	aggregateTimeout: 300,
	poll: 1000,
};

config.devServer = {
	port: 3000,
	setup(app) {
		app.get('/assets/config/config.json', function (req, res) {
			const configFile = require('./../src/assets/config/config.json');
			configFile.serverUrl = 'http://localhost:8888';
			res.json(configFile);
		});
	},
	proxy: {
		'/api/v1/semantic': {
			target: 'http://dev.data-prep-ee.talend.lan:9999/gateway/dq/semanticservice',
			changeOrigin: true,
			secure: false,
		},
		'/api/v1/stream-websocket': {
			target: process.env.API_URL || 'http://localhost',
			ws: true,
		},
		'/api': {
			target: process.env.API_URL || 'http://localhost',
			changeOrigin: true,
			secure: false,
		},
	},
	stats: {
		children: false,
	},
	historyApiFallback: true,
};

module.exports = config;
