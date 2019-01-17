import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.BehaviorSubject

fun main(args: Array<String>) {

    val filterSubject = BehaviorSubject.createDefault<Filter>(Filter.Disconnected)

    // can emit how many filters we want to the Observable, so for
    // each item in the array we will apply the current filter

    filterSubject.onNext(Filter.Connected(Handset("123", "Content")))
    filterSubject.onNext(Filter.Disconnected)  // <- current filter

    // the Observable will know the data source and only the last filter sent
    Observable.just(1, 2, 3, 4, 5)
        .withLatestFrom(filterSubject)
        .subscribeBy(
            // the result is a pair of the data and the filter
            onNext = { result ->
                val filter = result.second
                when(filter) {
                    is Filter.Connected -> println("handset ${filter.handset.id} is connected, payload: ${result.first}")
                    is Filter.Disconnected -> println("disconnected")
                }
            }
        )
}


/**
 * this withLatest From can accept subjects :)
 */
//fun <T, U> Observable<T>.withLatestFrom(other: ObservableSource<U>): Observable<Pair<T,U>>
//        = withLatestFrom(other, BiFunction{ t, u -> Pair(t,u)  })


/**
 * The filter sealed class holds the different filter types
 */
sealed class Filter {
    data class Connected(val handset: Handset) : Filter()
    object Disconnected : Filter()
}

/**
 * that's our data source
 */
data class Handset(val id: String, val content: String)