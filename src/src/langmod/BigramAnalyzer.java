package langmod;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import util.Bigram;
import util.BigramCorpus;
import util.Constants;
import util.FileProcessor;
import util.TextProcessor;

public class BigramAnalyzer extends Analyzer {
	private BigramCorpus _trainingData;
	private BigramCorpus _devData;
	private UnigramAnalyzer _unigramModel;
	
	private double _beta;

	public BigramAnalyzer(UnigramAnalyzer unigramModel) throws IOException {
		super(unigramModel.getTrainingFile(),unigramModel.getDevDataFile());
		this.initialize(_trainingDataPath, _devDataPath, unigramModel);
		_beta = this.bestBeta();
		// TODO Auto-generated constructor stub
	}
	public BigramAnalyzer(UnigramAnalyzer unigramModel,double beta) throws IOException {
		super(unigramModel.getTrainingFile(),unigramModel.getDevDataFile());
		this.initialize(_trainingDataPath, _devDataPath, unigramModel);
		_beta = beta;
		// TODO Auto-generated constructor stub
	}

	
	private void initialize(String trainingDataPath, String devDataPath, UnigramAnalyzer unigramModel) throws IOException{
		_trainingData = new BigramCorpus(trainingDataPath);
		_devData = new BigramCorpus(devDataPath);
		_unigramModel = unigramModel;
	}
	private double getTheta(Bigram b, double beta, BigramCorpus c) {
		int n_w_wprime = c.getBigramFrequency(b);
		int n_w_o = c.firstTokenFrequency(b.token1);
		double theta_wprime = _unigramModel.getTheta(b.token2, _unigramModel.getAlpha(), c);
		return (n_w_wprime + beta*theta_wprime)/(n_w_o + beta);
	}
	
	private double getTheta(String w, String wprime, double beta, BigramCorpus c) {
		return this.getTheta(new Bigram(w,wprime), beta, c);
		
		
	}
	public double bestBeta() {
		return util.MathUtil.ternarySearch(new MaximumLikelihoodFunction(), Math.pow(10,-6), Math.pow(10,6), Math.pow(10, -9));
	}
	
	public double getLogProbability(Vector<String[]> text) {
		double currprob = 0;
		for(String[] line: text) {
			currprob += Math.log(this.getTheta(Constants.STOP_TOKEN,line[0], _beta, _trainingData));
			for (int i = 0;i < line.length-1; i++) {
				currprob += Math.log(this.getTheta(line[i],line[i+1], _beta, _trainingData));
			}
			currprob += Math.log(this.getTheta(line[line.length-1],Constants.STOP_TOKEN, _beta, _trainingData));
		}
		return currprob;
	}
	
	private class MaximumLikelihoodFunction implements MaximizeableFunction {
		private Set<Bigram> _commonKeys;
		public MaximumLikelihoodFunction() {
		}
		@Override
		public double function(double beta) {
			double currSum = 0.0;
			Set<Bigram> bigrams = _devData.getBigrams();
			for(Bigram b: bigrams) {
				int frequency = _devData.getBigramFrequency(b);
				double logTheta = Math.log(BigramAnalyzer.this.getTheta(b, beta, _trainingData));
				currSum += frequency*logTheta;
			}
			return currSum;
		}
		
	}
	
	public double getCorpusProbability(String corpusPath) throws IOException {
		Vector<String[]> text = new Vector<String[]>();
		FileProcessor.processByLine(corpusPath, new TextProcessor(text));
		return this.getLogProbability(text);
	}
	public double getBeta() {
		// TODO Auto-generated method stub
		return _beta;
	}
	public BigramAnalyzer setOptimalBeta() {
		_beta = this.bestBeta();
		return this;
	}
	public BigramAnalyzer setBeta(double beta) {
		_beta = beta;
		return this;
	}
}
