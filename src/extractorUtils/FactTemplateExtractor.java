package extractorUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javatools.administrative.Announce;
import javatools.datatypes.Pair;
import basics.Fact;
import basics.FactCollection;
import basics.FactSource;

/**
 * YAGO2s - FactTemplateExtractor
 * 
 * Extracts from strings by help of fact templates
 * 
 * @author Fabian M. Suchanek
 *
 */
public class FactTemplateExtractor {

	/** List of patterns*/
	public final List<Pair<Pattern, List<FactTemplate>>> patterns=new ArrayList<>();

	/** Constructor 
	 * @throws IOException */
	public FactTemplateExtractor(FactSource  facts, String relation) throws IOException {
		this(new FactCollection(facts),relation);
	}
	
	/** Constructor */
	public FactTemplateExtractor(FactCollection facts, String relation) {
		Announce.doing("Loading fact templates of",relation);
		for (Fact fact : facts.get(relation)) {
			patterns.add(new Pair<Pattern, List<FactTemplate>>(fact.getArgPattern(1), FactTemplate.create(fact.getArgJavaString(2))));
		}
		if(patterns.isEmpty()) {
			Announce.warning("No patterns found!");
		}
		Announce.done();
	}

	/** Extractor*/
	public Collection<Fact> extract(String string, String dollarZero) {
		List<Fact> result=new ArrayList<>();
		for(Pair<Pattern, List<FactTemplate>> pattern : patterns) {
			Matcher m=pattern.first().matcher(string);
			while(m.find()) {
				Map<String,String> variables=new TreeMap<>();
				variables.put("$0", dollarZero);
				for(int i=1;i<=m.groupCount();i++) variables.put("$"+i,m.group(i));
				result.addAll(FactTemplate.instantiate(pattern.second(), variables));
			}
		}
		return(result);
	}
}