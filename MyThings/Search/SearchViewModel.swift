//
//  SearchViewModel.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/21.
//
import Foundation
import Combine

final class SearchViewModel: ObservableObject {
  @Published var items: [SearchItem] = []
  var subscriptions = Set<AnyCancellable>()
  
  var searchTerm: String = ""
  @Published var isLoading: Bool = false
  
  private let searchTappedSubject = PassthroughSubject<Void, Error>()
  private var disposeBag = Set<AnyCancellable>()
  
  // Data => Output
  //  @Published private(set) var items = [SearchItem]()
  
  let network: NetworkService
  init(network: NetworkService) {
    self.network = network
    searchTappedSubject
    .flatMap {
        self.search(searchText: self.searchTerm)
            .handleEvents(receiveSubscription: { _ in
                DispatchQueue.main.async {
                    self.isLoading = true
                }
            },
            receiveCompletion: { comp in
                DispatchQueue.main.async {
                    self.isLoading = false
                }
            })
            .eraseToAnyPublisher()
    }
    .map { $0.items }
    .replaceError(with: [])
    .receive(on: DispatchQueue.main)
    .assign(to: \.items, on: self)
    .store(in: &disposeBag)
  }
  
  func onSearchTapped() {
    searchTappedSubject.send(())
  }
  
  //  func getPosts() {
  //    guard let url = URL(string: "https://jsonplaceholder.typicode.com/posts") else { return }
  //
  //    URLSession.shared.dataTaskPublisher(for: url)
  //      .subscribe(on: DispatchQueue.global(qos: .background))
  //      .receive(on: DispatchQueue.main)
  //      .tryMap { data, response -> Data in
  //        guard
  //          let response = response as? HTTPURLResponse,
  //          response.statusCode >= 200 && response.statusCode < 300 else {
  //          throw URLError(.badServerResponse)
  //        }
  //        return data
  //      }
  //      .decode(type: SearchModel.self, decoder: JSONDecoder())
  //      .sink { completion in
  //        print("Completion: \(completion)")
  //      } receiveValue: { [weak self] returnedPost in
  ////        self?.posts = returnedPost
  //      }
  //      .store(in: &subscriptions)
  //
  //  }
  
  func search(searchText: String) -> AnyPublisher<TotalInfo, Error> {
    let resource: Resource<TotalInfo> = Resource(
      base: "https://433c-183-98-186-73.jp.ngrok.io",
      path: "/items",
      params: ["query": searchText],
      header: ["Content-Type": "application/json"]
    )
    return network.load(resource)
    //      .map { $0.items }
    //          .print("nettttotototo >>> \(items)")
    //          .replaceError(with: [])
    //          .receive(on: RunLoop.main)
    //          .assign(to: \.items, on: self)
    //          .store(in: &subscriptions)
//      .map { $0 }
//      .mapError { $0 as Error }
//      .decode(type: [TotalInfo].self, decoder: JSONDecoder())
//      .map { searchTerm.isEmpty ? $0 : $0.filter { $0.title.contains(searchTerm) } }
//      .eraseToAnyPublisher()
//  }
}
}
