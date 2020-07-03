const path = require('path');

module.exports = {
  entry: './src/main.ts',
  module: {
    rules: [
      { test: /\.ts$/, use: 'ts-loader', exclude: /node_modules/ },
    ],
  },

  mode: 'development',

  resolve: {
    extensions: [ '.ts', '.js' ],
  },

  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: 'bundle.js'
  },
};
