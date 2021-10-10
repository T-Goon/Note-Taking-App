var express = require('express');
var router = express.Router();
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');

const User = require('../schemas/users');
const Note = require('../schemas/notes');
const Image = require('../schemas/images');

function generateToken(user) {
  return jwt.sign({
    id: user.id,
    username: user.username,
  },
    process.env.JWT_SECRET);
}

router.post('/login', async (req, res, next) => {
  const { username, password } = req.body;

  console.log("Login input", username, password);

  const user = await User.findOne({ username });

  if (user) {
    const match = await bcrypt.compare(password, user.passwordHash);

    if (match) {
      console.log("User Logged in")
      res.json({ token: generateToken({ id: user._id, username: user.username }) });
    } else {
      console.log("Password not matching")
      res.json({ error: 'Password does not match' });
    }
    return;

  } else {
    const passwordHash = await bcrypt.hash(password, 12);

    let newUser = await new User({
      username,
      passwordHash,
    });

    newUser = await newUser.save();
    console.log("Login Success");
    res.json({ token: generateToken({ id: newUser._id, username: newUser.username }) });
  }
});

// Save notes to the database
router.post('/save', async function (req, res, next) {
  const { username, notes, token } = req.body;

  if (token) {
    try {
      jwt.verify(token, process.env.JWT_SECRET);

    } catch {
      console.log("Token is invalid: ", token);
      res.json({ error: "Token is invalid" });
      return;
    }
  } else {
    console.log("No token: ", token);
    res.json({ error: "No token" });
    return;
  }

  const user = await User.findOne({ username });

  notes.forEach(async (note) => {

    let oldNote = null;
    let image = null;
    if (note.image) {
      oldNote = await Note.findOne({ appId: note.appId });

      if (!oldNote || (oldNote && !oldNote.imageId)) {
        image = await new Image({
          image: note.image
        });
        await image.save();

      } else {
        image = await Image.findOneAndUpdate(
          { _id: oldNote.imageId },
          {
            image: note.image
          }, {
          new: true,
          upsert: true
        });
      }
    }

    await Note.findOneAndUpdate(
      { appId: note.appId },
      {
        appId: note.appId,
        title: note.title,
        body: note.body,
        location: note.location,
        imageId: image ? image._id : null,
        owner: user._id
      },
      {
        upsert: true
      }
    );

  });

  console.log("Save success", username, token);
  res.json({ success: true });
});

// Send notes to the client
router.post('/load', async function (req, res, next) {
  const { token } = req.body;

  let user;
  if (token) {
    try {
      user = jwt.verify(token, process.env.JWT_SECRET);

    } catch {
      console.log("Token is invalid: ", token);
      res.json({ error: "Token is invalid" });
      return;
    }
  } else {
    console.log("No token: ", token);
    res.json({ error: "No token" });
    return;
  }

  const notesFromDB = await Note.find({ owner: user.id });
  const resNotes = [];

  for(let i=0; i < notesFromDB.length; i++) {
    const image = await Image.findById(notesFromDB[i].imageId);
    
    resNotes.push({
      appId: notesFromDB[i].appId,
      title: notesFromDB[i].title,
      body: notesFromDB[i].body,
      image: image ? image.image : null,
      location: notesFromDB[i].location
    });
  }

  console.log("Load success", token, resNotes);
  res.json(resNotes);
});

module.exports = router;
