// Porting https://github.com/wouterbulten/slacjs/blob/e21748e5c11f1eb6357dc528bc60a4645ff09e22/src/app/models/particle-set.js

class ParticleSet {
  
  ///**
  // * Create a new particle set with a given number of particles
  // * @param  {Number} nParticles Number of particles
  // * @param  {Object} userConfig Config of the user
  // * @param  {Object} initConfig Config for the init filter
  // * @return {ParticleSet}
  // */
  //ParticleSet(nParticles, effectiveParticleThreshold, userConfig, initConfig) {
  //  this.nParticles = nParticles;
  //  this.effectiveParticleThreshold = effectiveParticleThreshold;
  //  this.particleList = [];

  //  //Internal list to keep track of initialised landmarks
  //  this.initialisedLandmarks = [];
  //  this.landmarkInitSet = new LandmarkInitializationSet(initConfig);

  //  for (let i = 0; i < nParticles; i++) {
  //    this.particleList.push(new Particle(userConfig));
  //  }
  //}

  ///**
  // * Given a control, let each particle sample a new user position
  // * @param  {[type]} control [description]
  // * @return {ParticleSet}
  // */
  //samplePose(control) {
  //  this.particleList.forEach((p) => p.samplePose(control));

  //  return this;
  //}

  ///**
  // * Let each particle process an observation
  // * @param  {object} obs
  // * @return {ParticleSet}
  // */
  //processObservation(obs) {

  //  if (obs !== {}) {

  //    const { uid, r, name, moved } = obs;

  //    //If the landmark has moved we remove it from all particles
  //    if (moved) {
  //      console.log('Moving landmark')
  //      this._removeLandmark(uid);
  //    }

  //    if (this.initialisedLandmarks.indexOf(uid) == -1) {

  //      const {x: uX, y: uY} = this.userEstimate();

  //      this.landmarkInitSet.addMeasurement(uid, uX, uY, r);

  //      const {estimate, x, y, varX, varY} = this.landmarkInitSet.estimate(uid);

  //      if (estimate > 0.6) {

  //        this.particleList.forEach((p) => {
  //          p.addLandmark(obs, {x, y}, {varX, varY});
  //        });

  //        this.landmarkInitSet.remove(uid);
  //        this.initialisedLandmarks.push(uid);
  //      }
  //    }
  //    else {
  //      this.particleList.forEach((p) => p.processObservation(obs));
  //    }
  //  }

  //  return this;
  //}

  ///**
  // * Resample the internal particle list using their weights
  // *
  // * Uses a low variance sample
  // * @return {ParticleSet}
  // */
  //resample() {

  //  const weights = this.particleList.map(p => p.weight);
  //  if (numberOfEffectiveParticles(weights) < this.effectiveParticleThreshold) {
  //    console.log('resampling');
  //    this.particleList = lowVarianceSampling(this.nParticles, weights).map((i) => {
  //      return new Particle({}, this.particleList[i]);
  //    });
  //  }

  //  return this;
  //}

  ///**
  // * Get particles
  // * @return {[Array]
  // */
  //particles() {
  //  return this.particleList;
  //}

  ///**
  // * Return the particle with the heighest weight
  // * @return {Particle}
  // */
  //bestParticle() {
  //  let best = this.particleList[0];

  //  this.particleList.forEach((p) => {
  //    if (p.weight > best.weight) {
  //      best = p;
  //    }
  //  });

  //  return best;
  //}

  ///**
  // * Compute an average of all landmark estimates
  // * @return {Map}
  // */
  //landmarkEstimate() {
  //  const weights = normalizeWeights(this.particleList.map((p) => p.weight));

  //  const landmarks = new Map();

  //  //Loop through all particles to get an estimate of the landmarks
  //  this.particleList.forEach((p, i) => {
  //    p.landmarks.forEach((landmark, uid) => {
  //      if (!landmarks.has(uid)) {
  //        landmarks.set(uid, {
  //          x: weights[i] * landmark.x,
  //          y: weights[i] * landmark.y,
  //          uid: uid,
  //          name: landmark.name
  //        });
  //      }
  //      else {
  //        const l = landmarks.get(uid);

  //        l.x += weights[i] * landmark.x;
  //        l.y += weights[i] * landmark.y;
  //      }
  //    });
  //  });

  //  return landmarks;
  //}

  ///**
  // * Get the best estimate of the current user position
  // * @return {object}
  // */
  //userEstimate() {
  //  const weights = normalizeWeights(this.particleList.map((p) => p.weight));

  //  return {
  //    x: this.particleList.reduce((prev, p, i) => prev + (weights[i] * p.user.x), 0),
  //    y: this.particleList.reduce((prev, p, i) => prev + (weights[i] * p.user.y), 0)
  //  };
  //}

  ///**
  // * Remove a landmark from all the particles
  // * @param  {String} uid Landmark uid
  // * @return {void}
  // */
  //_removeLandmark(uid) {

  //  //Remove from the landmark list if it exists
  //  const index = this.initialisedLandmarks.indexOf(uid);

  //  if (index != -1) {
  //    this.initialisedLandmarks.splice(index, 1);

  //    //Remove it from all particles
  //    this.particleList.forEach((p) => p.removeLandmark(uid));
  //  }
  //  else {

  //    //It is not initalised yet, so we remove it from the init set
  //    if (this.landmarkInitSet.has(uid)) {
  //      this.landmarkInitSet.remove(uid);
  //    }
  //  }
  //}
  
}