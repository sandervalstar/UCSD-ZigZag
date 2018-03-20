//import { randn, pdfn, variance } from '../util/math';
//import { lowVarianceSampling, numberOfEffectiveParticles, normalizeWeights } from '../util/sampling';
//import { polarToCartesian } from '../util/motion';

// Ported from https://github.com/wouterbulten/slacjs/blob/e21748e5c11f1eb6357dc528bc60a4645ff09e22/src/app/models/landmark-particle-set.js

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class LandmarkParticleSet {
  
  class Pair {
    
    private double x;
    private double y;
    
    public Pair(double x, double y) {
      this.x = x;
      this.y = y;
    }
    
    public double getX() {
      return this.x;  
    }
    
    public double getY() {
      return this.y;  
    }
  }
  
  private int numParticles;
  private int stdRange;
  private int randomParticles;
  private double effectiveParticleThreshold;
  private double maxVariance;
  private int measurements;
  private List<Particle> particles;
  
  public LandmarkParticleSet(int nParticles, int stdRange, int randomParticles, 
                            double effectiveParticleThreshold, double maxVariance) {
    this.numParticles = nParticles;
    this.stdRange = stdRange;
    this.randomParticles = randomParticles;
    this.effectiveParticleThreshold = effectiveParticleThreshold;
    this.maxVariance = maxVariance;
    this.measurements = 0;
    this.particles = new ArrayList<Particle>();
  }
  
  public void addMeasurement(double x, double y, double r) {
    if (this.measurements == 0) {
      this.particles = this.randomParticles(this.numParticles, x, y, r);  
    } else {
      this.updateWeights(x, y, r);
      List<Double> weights = this.getWeightMappings();
      
      if (this.numberOfEffectiveParticles(weights) < this.effectiveParticleThreshold) {
        List<Particle> randomSet = this.resample(this.numParticles - this.randomParticles);
        this.particles.addAll(randomSet);
      }
    }
    this.measurements++;
  }
  
   /**
   * Return the current estimate of this landmark's position
   * @return PositionEstimate
   */
  public PositionEstimate positionEstimate() {
    if (this.measurements < 10) {
      return new PositionEstimate (0, 0, 0, 1, 1);
    }
    
    final Pair pair = this.particleVariance();
    if (pair.getX() < this.maxVariance && pair.getY() < this.maxVariance) {
      Pair avg = this.averagePosition();
      return new PositionEstimate(1, avg.x, avg.y, pair.x, pair.y);
    }
    return new PositionEstimate(0, 0, 0, 1, 1);
  }
  
   /**
   * Return the particle with the heighest weight
   * @return {Particle}
   */
  public Particle bestParticle() {
    Particle best = this.particles.get(0);
    for (Particle tuple : this.particles) {
      if (tuple.weight > best.weight) {
        best = tuple;
      }
    }
    return best;
  }
  
  public Pair averagePosition() {
    List<Double> weights = normalizeWeights(this.getWeightMappings());
    
    int x;
    for (int i = 0; i < weights.size(); i++) {
      x += weights.get(i) * this.particles.get(i).getX(); //TODO: particle doesn't have an x???
    }
    int y;
    for (int i = 0; i < weights.size(); i++) {
      y += weights.get(i) * this.particles.get(i).getY(); //TODO: particle doesn't have a y???
    }
    return new Pair(x, y);
  }
  
  public Pair particleVariance() {
      return new Pair(varianceX(this.particles), varianceY(this.particles));
  }
  
  public List<Particle> resample(int nSamples) {
    List<Double> weights = this.getWeightMappings();
    Set<Integer> indices = lowVarianceSampling(nSamples, weights);
    List<Particle> particlesLocal = new ArrayList<Particle>();
    for (int i : indices) {
      //TODO: particle doesn't have an x???
      particlesLocal.add(new Particle(MathUtil.randn(this.particles.get(i).getX(), this.stdRange/4),
                                     MathUtil.randn(this.particles.get(i).getY(), this.stdRange/4), 1));
    }
    return particlesLocal;
  }
  
  public List<Double> getWeightMappings() {
    List<Double> weights = new ArrayList<Double>();
    for (Particle particle : this.particles) {
        weights.add(particle.weight);
    }
    return weights;
  }
  
  public List<Particle> randomParticles(int n, double x, double y, double r) {
    final double deltaTheta = 2 * Math.PI/n;
    final List<Particle> particles = new ArrayList<Particle>();
    for (int i = 0; i < n; i++) {
      double theta = i * deltaTheta;
      double range = r * MathUtil.randn(0, this.stdRange);
      Point2D.Double pair = MotionUtil.polarToCartesian(range, theta);
//      TODO: this constructor doesn't exist and Particle doesn't have x and y
      Particle particle = new Particle(x + pair.x, y + pair.y, 1);
      particles.add(particle);
    }
    return particles;
  }
  
  public void updateWeights(double x, double y, double r) {
    for (Particle p : this.particles) {
      //      TODO: Particle doesn't have x and y
      double dist = Math.sqrt(Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2));
      double weight = MathUtil.pdfn(r, dist, this.stdRange);
      p.weight = p.weight * weight;
    }
  }

  /**
 * Samples a new set using a low variance sampler from a array of weights
 * @param {Number} nSamples Number of samples to sample
 * @param {Array} weights   Weight array
 * @param //nSamples
   *@param weights @return {Array} An array with indices corresponding to the selected weights
 */
  public Set<Integer> lowVarianceSampling(int nParticles, List<Double> weights) {

    int M = weights.size();
    List<Double> normalizedWeights = normalizeWeights(weights);

    double rand = Math.random() * (1 / M);

    double c = normalizedWeights.get(0);
    int i = 0;

    Set<Integer> set = new HashSet();

    for (int m = 1; m <= nParticles; m++) {
      double U = rand + (m - 1) * (1 / M);

      while (U > c) {
        i = i + 1;
        c = c + normalizedWeights.get(i);
      }

      set.add(i);
    }

    return set;
  }

  public double numberOfEffectiveParticles(List<Double> weights) {
    List<Double> normalisedWeights = normalizeWeights(weights);
    
    double total = 0;
    for (double i : normalisedWeights) {
      total += (i*i);
    }
    return 1 / total;
  }
  
  List<Double> normalizeWeights(List<Double> weights) {
    double totalWeight = 0;
    for (double i : weights) {
      totalWeight += i;
    }
    List<Double> weights2 = new ArrayList<Double>();
    for (double i : weights) {
      weights2.add(i / totalWeight);
    }
    return weights2;
  }
  
   /**
   * Calculate the variance of an array given a value function
   * @param  {Array} data
   * @param  {Function} valueFunc Function that maps an array element to a number
   * @return {Number}
   */
   public double varianceX(List<Particle> data) {
  
    int sum = 0;
    double sumSq = 0;
    int n = data.size();
  
    for (Particle part : data) {
//      TODO: Particle doesn't have x
      int value = part.x;
      sum += value;
      sumSq += (value * value);
    }
  
    return (sumSq - ((sum * sum) / n)) / n;
  }
  
     /**
   * Calculate the variance of an array given a value function
   * @param  {Array} data
   * @param  {Function} valueFunc Function that maps an array element to a number
   * @return {Number}
   */
   public double varianceY(List<Particle> data) {
  
    int sum = 0;
    double sumSq = 0;
    int n = data.size();
  
    for (Particle part : data) {
//      TODO: Particle doesn't have y
      int value = part.y;
      sum += value;
      sumSq += (value * value);
    }
  
    return (sumSq - ((sum * sum) / n)) / n;
  }
}