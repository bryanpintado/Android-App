PhotosApp Android Port

Quick Project Recap

Single-user photo app with albums, tags (person/location), sliding through photos, moving photos between albums, and a tag-based search (AND/OR + autocomplete).

How I Used ChatGPT

I treated ChatGPT like a pair-programming partner—always checking its suggestions before pasting them in. Here are the prompts I actually gave:

"What's the best way to structure my Android Studio project based on the PhotosFX spec?"

"Show me step-by-step how to let the user move a photo from one album to another."

"Write a searchPhotos method that accepts two tag criteria and does AND/OR prefix matching."

"How can I pull a real filename (caption) from a content URI in Android?"

"What small edit should I make in PhotoAdapter to show photo.getCaption() instead of the raw URI?"

I’d copy the snippet, test it, tweak it, and then commit—never blindly pasting.

Final Thoughts

Using ChatGPT saved me tons of time researching Android APIs and writing boilerplate. I still did most of the logic and glue code by hand—and I learned a ton by tweaking and debugging each AI suggestion.
