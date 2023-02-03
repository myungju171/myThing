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
  @Published var searchTerm: String = ""
  var subscriptions = Set<AnyCancellable>()
  private var disposeBag = Set<AnyCancellable>()
  let network: NetworkService
  init(network: NetworkService) {
    self.network = network
  }
  func search(searchText: String) {
    let resource: Resource<TotalInfo> = Resource(
      base: Endpoint.baseURL,
      path: "/items",
      params: ["query": searchText],
      header: ["Content-Type": "application/json"]
    )
    network.load(resource)
      .print()
      .map { $0.items }
      .print()
      .replaceError(with: [])
      .receive(on: DispatchQueue.main)
      .assign(to: \.items, on: self)
      .store(in: &disposeBag)
  }
}
