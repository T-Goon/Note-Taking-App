const { model, Schema } = require('mongoose');

const NotesSchema = new Schema(
    {   
        appId: {
            type:String,
            unique: true,
            required: true
        },
        title: {
            type: String,
            required: true
        },
        body: {
            type: String
        },
        location:{
            type: String
        },
        imageId: {
            type: Schema.Types.ObjectId,
            ref: 'images'
        },
        owner: {
            type: Schema.Types.ObjectId,
            ref: 'users'
        }
    }
);

module.exports = model('notes', NotesSchema);