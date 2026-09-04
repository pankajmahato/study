## We need a data structure that:

1.) Consumes a stream of operations (contentId, action) where action ∈ {increasePopularity, decreasePopularity}.

2.) Tracks popularity (an integer) of each contentId.

3.) Can return the most popular contentId at any time.
    * If multiple have the same popularity → return any one of them.
    * If no content has popularity > 0 → return -1.