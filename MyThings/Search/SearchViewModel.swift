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
  func search(searchText: String) -> AnyPublisher<TotalInfo, Error> {
    let resource: Resource<TotalInfo> = Resource(
      base: Endpoint.baseURL,
      path: "/items",
      params: ["query": searchText],
      header: ["Content-Type": "application/json"]
    )
    return network.load(resource)
  }
}
