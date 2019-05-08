var chars = document.querySelectorAll('.text span');

TweenMax.staggerFrom(chars, 0.5, {
  y: 100,
  yoyo: true,
  repeat: -1
}, 0.2);

TweenMax.staggerFrom(chars, 1, {
  rotation: 360,
  repeat: -1
}, 0.2);