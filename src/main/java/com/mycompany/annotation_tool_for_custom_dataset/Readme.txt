The Input CSV file must have ID and corresponding Reviews (First columns ID and Second Columns should be Sentence).

{
  "domain": "example_domain",
  "input_data_path": "path/to/input/data.csv",
  "Sentiment_Analysis_or_Text_Annotataion_under_classes": {
    "Simple_Annotation": true,
    "Classes(Sentiments_or_aspects)": ["Class A", "Class B", "Class C"]
  },
  "Aspect_based_sentiment_Analysis": {
    "Aspect_based_Sentiment_Analysis": false,
    "Categories_wrapper": {
      "wrap_aspects_in_categories": true,
      "categories": ["Category A", "Category B", "Category C"],
      "aspects": {
        "Category A": ["Aspect 1", "Aspect 2", "Aspect 3", "Aspect 4"],
        "Category B": ["Aspect X", "Aspect Y", "Aspect Z"],
        "Category C": ["Aspect I", "Aspect II", "Aspect III"]
      }
    },
    "Classes(Sentiments_or_aspects)": ["Class A", "Class B", "Class C"],
    "sentiments": ["Positive", "Neutral", "Negative", "Very Negative", "Very Positive"]
  },
  "opinion_term": false,
  "split_Reviews_to_sentences": false,
  "text_preprocessing": {
    "remove_html": true,
    "remove_urls": true,
    "remove_emails": true,
    "remove_mentions": true,
    "remove_punctuations": true,
    "custom_stopwords_to_remove": ["the", "and", "of"],
    "remove_extra_spaces": true
  }
}
