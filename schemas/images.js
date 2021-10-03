const { model, Schema } = require('mongoose');

const ImagesSchema = new Schema(
    {
        image: {
            type: String,
            required: true
        }
    }
);

module.exports = model('images', ImagesSchema);